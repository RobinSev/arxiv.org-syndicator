package app.arxivorg.model.filters.nonapifilters;

import app.arxivorg.model.Article;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FilterByDate implements NonAPIFilter {

    private final LocalDate date;

    public FilterByDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Given a list of articles, removes all the articles that are published before the date in attribute.
     *
     * @param articlesToFilter : the list of articles to filter.
     * @return the list of the articles that are published after the date in attribute.
     */
    @Override
    public List<Article> filter(List<Article> articlesToFilter) {
        List<Article> filteredArticles = new ArrayList<>();
        for (Article article : articlesToFilter) {
            LocalDate articleDate = article.getPublicationDate();
            if (articleDate.isAfter(date) || articleDate.isEqual(date))
                filteredArticles.add(article);
        }
        return filteredArticles;
    }

    /**
     * Test if the NonAPIFilter is equals to another java object.
     * Override the Object equals.
     *
     * @param o : the object we want to test the equality with.
     * @return {@code true} if they are the same type of NonAPIFilter, and if they have the same date attribute,
     * {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FilterByDate)) return false;
        FilterByDate filterByDate = (FilterByDate) o;
        return date.isEqual(filterByDate.date);
    }

}
