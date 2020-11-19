package app.arxivorg.model.filters.nonapifilters;

import app.arxivorg.model.Article;

import java.util.List;

public interface NonAPIFilter {

    /**
     * Given a list of articles, removes all the articles that do not fit the criterion of the NonAPIFilter.
     *
     * @param articlesToFilter : the list of articles to filter.
     * @return the list of the articles that fit the filter criterion.
     */
    List<Article> filter(List<Article> articlesToFilter);

}
