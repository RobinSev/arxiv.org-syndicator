package app.arxivorg.model;

import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import app.arxivorg.model.userintelhandler.UserIntel;
import app.arxivorg.model.userintelhandler.UserIntelHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RequestHandler {

    private final String INITIAL_QUERY = "http://export.arxiv.org/api/query?search_query=";
    private final int DEFAULT_START = 0;
    private List<APIFilter> lastAPIFiltersUsed;
    private List<NonAPIFilter> lastNonAPIFiltersUsed;
    private final int LIMIT_OF_ARTICLES_PER_REQUEST = 1000;
    private int numberOfArticlesOfLastRequest = 50;
    private int finalStartOfLastRequest = 0;

    RequestHandler() {
        UserIntelHandler userIntelHandler = new UserIntelHandler(
                "src/main/java/app/arxivorg/model/resources/userIntel.json");
        UserIntel userIntel = null;
        try {
            userIntel = userIntelHandler.getUserIntel();
        } catch (IOException ignored) {}

        if(userIntel != null) {
            this.lastNonAPIFiltersUsed = userIntel.getLastNonAPIFiltersUsed();
            this.lastAPIFiltersUsed = userIntel.getLastAPIFiltersUsed();
        }
        else {
            this.lastAPIFiltersUsed = new ArrayList<>();
            this.lastNonAPIFiltersUsed = new ArrayList<>();
        }
    }

    /**
     * Generate and send a request to the arxiv.org API server. Then filters the received list of Article
     * so that it would fit the NonAPIFilter constraints.
     * This method tries to fill the result list with the number of articles asked in parameter. However, if the filters
     * are really specific, the API may not have enough articles that fit the filters.
     * Thus, we can only assert that the result list will not have more than the number of articles asked. But it may
     * have less.
     *
     * @param apiFilters : the list of the APIFilter to use for this request. An Empty list if none are to use.
     * @param nonAPIFilters : the list of the NonAPIFilter to use for this request. An Empty list if none are to use.
     * @param numberOfArticles : the number of articles to harvest with this request.
     *
     * @return  a list of articles corresponding to all the criteria, with at most the number of articles required (can
     *          be less depending of the precision of the filters used). {@code null} if the number of articles requested
     *          is too high for the API.
     *
     * @throws Exception in case the request failed to be send or the stream is interrupted.
     */
    public List<Article> request (List<APIFilter> apiFilters, List<NonAPIFilter> nonAPIFilters, int numberOfArticles)
            throws Exception {
        storeParameters(apiFilters, nonAPIFilters, numberOfArticles);
        return request(apiFilters, nonAPIFilters, DEFAULT_START, numberOfArticles, new ArrayList<>());
    }


    /**
     * Get the articles that follow the ones sent by the previous call to the request method. Uses the same parameters
     * as the previous request, for consistency between the two results.
     * During a requestMoreArticle request, there is a chance that new articles were published in the arxiv database
     * between the first and the second call to the request method. And, in doing so, our method may return articles
     * already harvested in the previous request. To avoid any duplicate, a list of the articles sent by the previous
     * request is given in parameter.
     *
     * @param articlesPreviouslyHarvested : articles already harvested by the previous call to the request method in the
     *                                   model.
     *
     * @return  a list of articles corresponding to all the criteria, with at most the number of articles required (can
     *          be less depending of the precision of the filters used). {@code null} if the number of articles requested
     *          is too high for the API.
     *
     * @throws Exception in case the request failed to be send or the data stream is interrupted.
     */
    public List<Article> requestMoreArticles(List<Article> articlesPreviouslyHarvested) throws Exception {
        return request(lastAPIFiltersUsed, lastNonAPIFiltersUsed, finalStartOfLastRequest, numberOfArticlesOfLastRequest,
               articlesPreviouslyHarvested);
    }


    /**
     * Generate and send a request to the arxiv.org API server. Then filters the received list of Article
     * so that it would fit the NonAPIFilter constraints.
     * This method tries to fill the result list with the number of articles asked in parameter. However, if the filters
     * are really specific, the API may not have enough articles that fit the filters.
     * Thus, we can only assert that the result list will not have more than the number of articles asked. But it may
     * have less.
     * During a requestMoreArticle request, there is a chance that new articles were published in the arxiv database
     * between the first and the second request. And, in doing so, our method may return articles already harvested
     * in the previous request. To avoid any duplicate, a list of articles is added in parameter.
     *
     * @param apiFilters : the list of the APIFilter to use for this request. An Empty list if none are to use.
     * @param nonAPIFilters : the list of the NonAPIFilter to use for this request. An Empty list if none are to use.
     * @param start : the first articles to consider in the list of articles stored in arxiv.org API servers.
     * @param numberOfArticles : the number of articles to harvest with this request.
     * @param articlesPreviouslyHarvested : articles already harvested by the previous call to the request method in the
     *                                   model. May be empty.
     *
     * @return  a list of articles corresponding to all the criteria, with at most the number of articles required (can
     *          be less depending of the precision of the filters used). {@code null} if the number of articles requested
     *          is too high for the API.
     *
     * @throws Exception in case the request failed to be send or the stream is interrupted.
     */
    private List<Article> request(List<APIFilter> apiFilters, List<NonAPIFilter> nonAPIFilters, int start,
                                  int numberOfArticles, List<Article> articlesPreviouslyHarvested)
            throws Exception {
        if (numberOfArticles < 1) return null;
        if (numberOfArticles > LIMIT_OF_ARTICLES_PER_REQUEST) return null;

        List<Article> articles = new ArrayList<>();
        boolean breakAtEndOfLoop = false;

        while (!hasEnoughArticles(articles, numberOfArticles) && !breakAtEndOfLoop) {

            String request = buildRequestWithParameters(apiFilters, start, numberOfArticles);
            List<Article> articlesToEvaluate = sendRequest(request);

            if(apiDoesNotHaveMore(articlesToEvaluate, numberOfArticles)) breakAtEndOfLoop = true;

            for (NonAPIFilter nonAPIFilter : nonAPIFilters) {
                int formerSize = articlesToEvaluate.size();
                articlesToEvaluate = nonAPIFilter.filter(articlesToEvaluate);
                if (filterByDateRemovedSomeArticles(nonAPIFilter, articlesToEvaluate, formerSize))
                    breakAtEndOfLoop = true;
            }

            addToResultListIfNotPresent(articles, articlesToEvaluate, articlesPreviouslyHarvested);

            start += numberOfArticles;
        }

        removeSupernumeraryArticles(articles, numberOfArticles);
        this.finalStartOfLastRequest = start;
        return articles;
    }

    //The condition tested only applies as long as the articles sent by Arxiv are sorted by publication date descending.
    private List<Article> applyNonAPIFilters(List<Article> articlesToFilter, List<NonAPIFilter> filtersToApply,
                                             boolean breakAtEndOfLoop) {
        for (NonAPIFilter nonAPIFilter : filtersToApply) {
            int formerSize = articlesToFilter.size();
            articlesToFilter = nonAPIFilter.filter(articlesToFilter);
            if(filterByDateRemovedSomeArticles(nonAPIFilter, articlesToFilter, formerSize)) {
                //breakAtEndOfLoop = true;
            }
        }
        return articlesToFilter;
    }

    private boolean filterByDateRemovedSomeArticles(NonAPIFilter nonAPIFilter, List<Article> filteredArticles, int formerSize) {
        return nonAPIFilter instanceof FilterByDate && filterRemovedSomeArticles(filteredArticles, formerSize);
    }

    private boolean filterRemovedSomeArticles(List<Article> filteredArticles, int formerSize) {
        return filteredArticles.size() < formerSize;
    }

    private void addToResultListIfNotPresent(List<Article> resultList, List<Article> articlesToEvaluate,
                                             List<Article> articlesPreviouslyHarvested) {

        for(Article article : articlesToEvaluate)
            if (!resultList.contains(article) && !articlesPreviouslyHarvested.contains(article))
                resultList.add(article);
    }

    private void storeParameters(List<APIFilter> apiFilters, List<NonAPIFilter> nonAPIFilters, int numberOfArticles) {
        this.lastAPIFiltersUsed = apiFilters;
        this.lastNonAPIFiltersUsed = nonAPIFilters;
        this.numberOfArticlesOfLastRequest = numberOfArticles;
    }


    private boolean apiDoesNotHaveMore(List<Article> articlesToTest, int numberOfArticles) {
        return articlesToTest.size() < numberOfArticles;
    }

    private boolean hasEnoughArticles(List<Article> articles, int requiredNumberOfArticles) {
        return articles.size() >= requiredNumberOfArticles;
    }


    private List<Article> sendRequest(String request) throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest query = HttpRequest.newBuilder()
                    .uri(URI.create(request))
                    .build();

        HttpResponse<String> response;
        try {
            response = client.send(query, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new Exception("The request to Arxiv could not be sent. Try again later and if the problem persist," +
                    " please contact technical support");
        }
        String string = response.body();
        return AtomParser.parse(string);
    }


    /**
     * Create the request to be sent to arxiv.org API servers. If the apiFilters list is not empty, the result is
     * sorted by relevance. Otherwise, the result is sorted by publication date.
     *
     * @param apiFilters : the list of the APIFilter to use for this request. An Empty list if none are to use.
     * @param start : the first articles to consider in the list of articles stored in arxiv.org API servers.
     * @param numberOfArticles : the number of articles to harvest with this request.
     *
     * @return a String representation of the completed request to send.
     */
    private String buildRequestWithParameters(List<APIFilter> apiFilters, int start, int numberOfArticles) {

        String finalQuery = "";

        if (apiFilters.isEmpty())
            finalQuery += "all";
        else {
            for (APIFilter filter : apiFilters) {
                finalQuery = filter.addParametersToQuery(finalQuery) + " AND ";
            }
            finalQuery = removeFinalAND(finalQuery);
        }

        finalQuery = INITIAL_QUERY + URLEncoder.encode(finalQuery, StandardCharsets.UTF_8);

        finalQuery += "&start=" + start;
        finalQuery += "&max_results=" + numberOfArticles;

        return sortResultsBySubmittedDate(finalQuery);
    }


    private String removeFinalAND(String request) {
        return request.substring(0, request.length() - 5);
    }


    private String sortResultsBySubmittedDate(String query) {
        return query + "&sortBy=submittedDate&sortOrder=descending";
    }

    private void removeSupernumeraryArticles(List<Article> articles, int numberOfArticles) {
        while (articles.size() > numberOfArticles)
            articles.remove(articles.size() - 1);
    }

}
