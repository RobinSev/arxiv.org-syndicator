package app.arxivorg.model;


import app.arxivorg.controller.DownloadOption;
import app.arxivorg.model.download.DownloadHandler;
import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import app.arxivorg.model.userintelhandler.UserIntel;
import app.arxivorg.model.userintelhandler.UserIntelHandler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Model {

    private final RequestHandler requestHandler = new RequestHandler();
    private UserIntelHandler userIntelHandler = new UserIntelHandler(
            "src/main/java/app/arxivorg/model/resources/userIntel.json");


    /**
     * Update the last filters used and the date of the last request saved.
     * Send a request to the arxiv.org API server and generate a list of Article that fit the constraints specified
     * in the parameters.
     *
     * @param apiFilters : the list of the APIFilter to use for this request. An Empty list if none are to use.
     * @param nonAPIFilters : the list of the NonAPIFilter to use for this request. An Empty list if none are to use.
     * @param numberOfArticles : the number of articles to harvest with this request.
     *
     * @return  a list of articles corresponding to all the criteria, with at most the number of articles required (can
     *          be less depending of the precision of the filters used). {@code null} if the start is to
     *          high for the API to handle.
     *
     * @throws Exception :  throws the exceptions thrown by UserIntelHandler and requestHandler methods called,
     *                      for the user interface to display.
     */
    public List<Article> requestArticles(List<APIFilter> apiFilters, List<NonAPIFilter> nonAPIFilters,
                                         int numberOfArticles) throws Exception {

        userIntelHandler.updateFiltersAndDateOfLastRequest(apiFilters, nonAPIFilters);
        return requestHandler.request(apiFilters, nonAPIFilters, numberOfArticles);
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
        userIntelHandler.updateOnlyDateOfLastRequest();
        return requestHandler.requestMoreArticles(articlesPreviouslyHarvested);
    }


    /**
     * Generate a UserIntel object containing the user intel saved.
     * The UserIntel only authorizes consulting the information. If a change has to be made, it has to be from other
     * methods of the Model, in order to ensure that the modifications are saved.
     *
     * @return a UserIntel Object containing the information saved.
     *
     * @throws IOException : throws the exceptions thrown by UserIntelHandler for the user interface to display.
     */
    public UserIntel getUserIntel() throws IOException {
        return userIntelHandler.getUserIntel();
    }


    /**
     * Add a new article in the favorites list saved.
     *
     * @return {@code true} if the article was not in the favorites, and was added and saved successfully.
     * {@code false} otherwise.
     *
     * @throws Exception : throws the exceptions thrown by UserIntelHandler for the user interface to display.
     */
    public boolean addFavorite(Article article) throws Exception {
        return userIntelHandler.addFavorite(article);
    }


    /**
     * Remove an article from the favorites list saved.
     *
     * @return {@code true} if the article was removed and the favorites saved successfully.
     * {@code false} otherwise.
     *
     * @throws Exception : throws the exceptions thrown by UserIntelHandler for the user interface to display.
     */
    public boolean removeFavorite(Article article) throws Exception {
        return userIntelHandler.removeFavorite(article);
    }


    /**
     * Download the articles in parameter starting at the absolute path specified. Depending of the option chosen,
     * creates the tree view of folders where to download each article in, starting at the absolute path
     * given in parameter.
     * If a tree view is generated, and in case of a download error, the folder created to download the article that
     * was not downloaded is deleted if empty.
     *
     * @param articlesToDownload : the list of articles to download.
     * @param folderPath : a string representation of the folder absolute path where to download the articles.
     * @param option : the download option picked in the enum DownloadOption.
     * @return a list of all the articles that were not successfully downloaded due to errors.
     */
    public List<Article> download(List<Article> articlesToDownload, String folderPath, DownloadOption option) {
        return DownloadHandler.download(articlesToDownload, folderPath, option);
    }


    /**
     * Give statistics by categories in percentage.
     *
     * @param listArticles : item list
     * @return A hasmap giving category statistics.
     */
    public Map<String, Float> statisticsByCategory(List<Article> listArticles) {
        return Statistics.statisticsByCategory(listArticles);
    }


    /**
     * Give the statistics of the number of articles per day, a given date will have for example 3 items.
     *
     * @param listArticles : item list
     * @return A hasmap giving the number of articles per day.
     */
    public Map<LocalDate, Float> statisticsByNumberOfArticlePerDay(List<Article> listArticles) {
        return Statistics.statisticsByNumberOfArticlePerDay(listArticles);
    }

    /**
     * Give the statistics of the number of articles an author to write.
     *
     * @param listArticles : item list
     * @return A hasmap giving The number of articles an author to write.
     */
    public Map<Author, Float> statisticsByAuthor(List<Article> listArticles) {
        return Statistics.statisticsByAuthor(listArticles);
    }


    /**
     * Gives statistics of expressions given by the user and returns their frequency in title.
     * This method also accepts regular expressions
     *
     * @param listArticles : item list
     * @return A hasmap giving how often these expressions are used in title.
     */
    public Map<String, Float> statisticsByExpressionInTitle(List<Article> listArticles, List<String> expressions) {
        return Statistics.statisticsByExpressionInTitle(listArticles, expressions);
    }

    /**
     * Gives statistics of expressions given by the user and returns their frequency in the summary.
     * This method also accepts regular expressions
     *
     * @param listArticles : item list
     * @return A hasmap giving how often these expressions are used in the summary.
     */
    public Map<String, Float> statisticsByExpressionInSummary(List<Article> listArticles, List<String> expressions) {
        return Statistics.statisticsByExpressionInSummary(listArticles, expressions);
    }

    /**
     * Used only to allow tests on the Model class.
     * @param newFilePath : the String representation of the path of the test file.
     */
    void setTestUserIntelHandlerPath(String newFilePath) {
        this.userIntelHandler = new UserIntelHandler(newFilePath);
    }

    String getUserIntelHandlerFilePath() {
        return userIntelHandler.getFilePath().toString();
    }

}
