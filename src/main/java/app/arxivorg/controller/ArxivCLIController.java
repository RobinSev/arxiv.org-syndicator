package app.arxivorg.controller;

import app.arxivorg.model.Article;
import app.arxivorg.model.filters.apifilters.*;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArxivCLIController extends Controller {

    private boolean isInterpreterMode;

    /**
     * Constructs an ArxivCLIController from the specified parameters.
     *
     * @param numbersArticlesRequest - The default number of articles to request to the model.
     * @param isInterpreterMode - {@code true} if the CLI is used in interpreter mode.
     */
    public ArxivCLIController(int numbersArticlesRequest, boolean isInterpreterMode) {
        super(numbersArticlesRequest);
        this.isInterpreterMode = isInterpreterMode;
    }


    public boolean isInterpreterMode() {
        return isInterpreterMode;
    }


    public List<Article> getArticles() {
        return listHandler.getArticles();
    }


    /**
     * Update the articles list in listHandler with the articles
     * provided by the model after have made a request with valid parameters.
     *
     * Returns the new list of articles if everything went right, null otherwise.
     *
     * @param options - The filters provided by the user in the CLI.
     * @return the new list of articles if no error are thrown, null otherwise.
     */
    public List<Article> requestArticles(Map<String, String> options) {
        List<APIFilter> apiFilters = getApiFilters(options);
        List<NonAPIFilter> nonAPIFilters = getNonApiFilters(options);
        if (apiFilters == null || nonAPIFilters == null) return null;
        try {
            List<Article> articles = requestWithFilters(apiFilters, nonAPIFilters, NUMBERS_OF_ARTICLES_TO_REQUEST);
            listHandler.setArticles(articles);
            listHandler.setSelected(new ArrayList<>());
            return articles;
        } catch (Exception e) {
            return null;
        }


    }

    /**
     * Downloads the articles in the listHandler's {@code selected} list.
     * Returns 0 if everything went right, -1 if the no articles could
     * be downloaded, -3 if there is a non-valid filter, or the exit code
     * of the download method otherwise.
     *
     * @param path - The path to the folder where the articles must be downloaded.
     * @param filters - The filters to apply to get the articles to download from the model.
     * @return an exit code specifying a code of error or 0 if everything went right.
     */
    public int downloadAll(String path, Map<String, String> filters) {
        List<APIFilter> apiFilters = getApiFilters(filters);
        List<NonAPIFilter> nonAPIFilters = getNonApiFilters(filters);
        if (apiFilters == null || nonAPIFilters == null) return -3;
        try {
            List<Article> requestedArticles = requestWithFilters(apiFilters, nonAPIFilters, NUMBERS_OF_ARTICLES_TO_REQUEST);
            listHandler.setArticles(requestedArticles);
            int notDownloaded = download(path, listHandler.getArticles(), DownloadOption.SAME_FOLDER);
            if (notDownloaded == requestedArticles.size()) return -1;
            return notDownloaded;
        } catch (Exception e) {
            return -2;
        }
    }

    /**
     * Downloads the articles in the listHandler's {@code selected} list.
     * Returns -1 if the no articles could be downloaded, or the exit code
     * of the download method otherwise.
     *
     * @param path - The path specifying the folder where the articles must be downloaded.
     * @return -1 if the no articles could be downloaded, or the exit code
     * of the download method otherwise.
     */
    public int downloadSelected(String path) {
        int notDownloaded = download(path, listHandler.getSelected(), DownloadOption.SAME_FOLDER);
        return notDownloaded == listHandler.getSelected().size() ? -1 : notDownloaded;
    }


    /**
     * Returns as a list of NonAPIFilters the filters not handled by the API
     * specified in the map {@code filters}, or null if the value of a field
     * is not valid.
     *
     * @param filters - The filters input by the user in the CLI.
     * @return as a list, the filters not handled by the API provided in the map.
     */
    private List<NonAPIFilter> getNonApiFilters(Map<String, String> filters) {
        List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
        if (filters.containsKey("period")) {
            if (!isValidDate(filters.get("period"))) return null;
            String[] splitDate = filters.get("period").split("-");
            LocalDate date = LocalDate.of(
                    Integer.parseInt(splitDate[0]),
                    Integer.parseInt(splitDate[1]),
                    Integer.parseInt(splitDate[2]));
            nonAPIFilters.add(new FilterByDate(date));
        }
        return nonAPIFilters;
    }


    /**
     * Returns as a list of APIFilters the filters handled by the API
     * specified in the map {@code filters}, or null if the value of a field
     * is not valid.
     *
     * @param filters - The filters input by the user in the CLI.
     * @return as a list, the filters handled by the API provided in the map.
     */
    private List<APIFilter> getApiFilters(Map<String, String> filters) {
        List<APIFilter> apiFilters = new ArrayList<>();
        if (filters.containsKey("category")) {
            if (!isValidCategory(filters.get("category"))) return null;
            apiFilters.add(new FilterByCategories(filters.get("category")));
        }
        if (filters.containsKey("keywords")) {
            APIFilter filterByTitle = new FilterByTitle(filters.get("keywords"));
            APIFilter filterByAbstract = new FilterByAbstract(filters.get("keywords"));
            apiFilters.add(filterByTitle);
            apiFilters.add(filterByAbstract);
        }
        if (filters.containsKey("authors")) {
            apiFilters.add(new FilterByAuthors(filters.get("authors")));
        }
        return apiFilters;
    }

    /**
     * Returns true if the input date has the good date format (yyyy-MM-dd)
     * and is not in the future or not three years before.
     *
     * @param dateToTest - The String input by the user as a date.
     * @return true if the specified string as the good format and if the corresponding date
     *          is in the right time interval.
     */
    private boolean isValidDate(String dateToTest) {
        if (!dateToTest.matches("^2[0-9]{3}-((0[1-9])|(1[0-2]))-(([012][0-9])|(3[01]))$"))
            return false;
        String[] splitDate = dateToTest.split("-");
        LocalDate date = LocalDate.of(
                Integer.parseInt(splitDate[0]),
                Integer.parseInt(splitDate[1]),
                Integer.parseInt(splitDate[2])
        );
        return date.isBefore(LocalDate.now()) && date.isAfter(LocalDate.now().minusYears(3));
    }

    /**
     * Returns {@code true} if the category is known by arXiv
     * (i.e. if the category input by the user is in the list of categories' codes).
     *
     * @param category - The category code input by the user.
     * @return true if the category code is known by ArXiv.
     */
    private boolean isValidCategory(String category) {
        return categoryParser.getCategoriesCodes().contains(category);
    }

    /**
     * Replaces the list of selected articles by the list of articles
     * at the specified indexes in the current list of articles and
     * returns the status of the called listHandler's setSelected method.
     *
     * @param indexes - The indexes of the corresponding articles to select.
     * @return the status of the listHandler's setSelected method.
     */
    public int setSelected(List<Integer> indexes) {
        return listHandler.setSelectedIndexes(indexes);
    }


}
