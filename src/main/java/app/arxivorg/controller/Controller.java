package app.arxivorg.controller;

import app.arxivorg.model.Article;
import app.arxivorg.model.Model;
import app.arxivorg.model.RequestHandler;
import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Controller {

    /**
     * Gives the opportunity to save the articles provided by the model
     * and saves the selected by the user.
     * Has methods to gets the articles as displayable items.
     */
    protected ListHandler listHandler;

    protected Model model;

    /**
     * An instance of category parser.
     * Contains a method to parse the categoryArXiv.txt file by
     * extracting categories, subcategories, and their arXiv codes.
     *
     */
    protected ParseCategory categoryParser;


    protected Map<String, List<String>> category;

    /**
     * The default number of articles to request to the model.
     */
    protected final int NUMBERS_OF_ARTICLES_TO_REQUEST;

    protected Controller(int numbersArticlesRequest) {
        this.model = new Model();
        this.NUMBERS_OF_ARTICLES_TO_REQUEST = numbersArticlesRequest;
        this.listHandler = new ListHandler();

        this.categoryParser = new ParseCategory("src/main/java/app/arxivorg/controller/categoryArXiv.txt");
        this.category = categoryParser.getCategories();
    }


    /**
     * Returns the list of articles provided by the model for the specified
     * {@code apiFilters} and {@code nonApiFilters}.
     *
     * @param apiFilters - The filters supported by the Arxiv api to apply in the request.
     * @param nonApiFilters - The unsupported filters to apply.
     * @param nbOfArticlesAsked - The number of article to request.
     *
     * @return the list of articles provided by the model for the specified filters.
     * @throws Exception - The exception throws by the model's request method.
     */
    public List<Article> requestWithFilters(List<APIFilter> apiFilters,
                                            List<NonAPIFilter> nonApiFilters,
                                            int nbOfArticlesAsked) throws Exception {
        return this.model.requestArticles(apiFilters, nonApiFilters, nbOfArticlesAsked);
    }

    /**
     * Downloads all the articles of the specified list.
     * Must be called by both GUIController and CLIController methods.
     *
     * @param path - The path where the articles must be downloaded.
     * @param articles - The list of articles to download.
     * @param option - The download option chosen by the user.
     * @return the number of successfully downloaded articles.
     */
    protected int download(String path, List<Article> articles, DownloadOption option) {
        int validationCode = areValidDownloadParameters(path, articles, option);
        if (validationCode != 0) return validationCode;
        List<Article> notDownloaded = model.download(articles, path, option);
        return notDownloaded.size();
    }

    /**
     * Checks if all the specified parameters are valid so that it can be used as parameters
     * in the download method of the model.
     * Returns a status code corresponding to the non-valid filter if there is one, 0 otherwise.
     * Precisely, it returns :
     * • 0 if all the specified parameters are valid.
     * • -1 if the path isn't absolute (i.e. doesn't match with the regular expression).
     * • -2 if the download option were not specified.
     * • -3 if there is no article to download.
     *
     * @param path - The path indicating the folder where the articles must be downloaded.
     * @param articles - The list of articles to download.
     * @param option - The download option specifying in the DownloadOption class.
     * @return a status code corresponding to the error if there is one, 0 otherwise.
     */
    private int areValidDownloadParameters(String path, List<Article> articles, DownloadOption option) {
        if (articles.isEmpty()) return -3;
        if (option == null) return -2;
        String sep = File.separator;
        if (!path.matches(sep+"(([\\w\\W\\s]+"+sep+"?)*)$")) return -1;
        return 0;
    }
}
