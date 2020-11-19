package app.arxivorg.model.userintelhandler;

import app.arxivorg.model.Article;
import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;

import java.time.LocalDate;
import java.util.List;

public class UserIntel {

    private LocalDate dateOfLastRequest;
    private List<APIFilter> lastAPIFiltersUsed;
    private List<NonAPIFilter> lastNonAPIFiltersUsed;
    private List<Article> favorites;

    public UserIntel(LocalDate dateOfLastRequest, List<APIFilter> lastAPIFiltersUsed,
                     List<NonAPIFilter> lastNonAPIFiltersUsed, List<Article> favorites) {
        this.dateOfLastRequest = dateOfLastRequest;
        this.lastAPIFiltersUsed = lastAPIFiltersUsed;
        this.lastNonAPIFiltersUsed = lastNonAPIFiltersUsed;
        this.favorites = favorites;
    }

    boolean addFavorite(Article article) {
        if(favorites.contains(article)) return false;
        return favorites.add(article);
    }

    boolean removeFavorite(Article article) { return favorites.remove(article); }

    void updateFiltersUsed(List<APIFilter> newAPIFilters, List<NonAPIFilter> newNonAPIFilters) {
        this.lastAPIFiltersUsed = newAPIFilters;
        this.lastNonAPIFiltersUsed = newNonAPIFilters;
    }

    void updateDateOfLastUsage(LocalDate newDate) { this.dateOfLastRequest = newDate; }

    public LocalDate getDateOfLastRequest() {
        return dateOfLastRequest;
    }

    public List<APIFilter> getLastAPIFiltersUsed() {
        return lastAPIFiltersUsed;
    }

    public List<NonAPIFilter> getLastNonAPIFiltersUsed() { return lastNonAPIFiltersUsed; }

    public List<Article> getFavorites() { return favorites; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserIntel)) return false;
        UserIntel userIntel = (UserIntel) o;
        return getDateOfLastRequest().equals(userIntel.getDateOfLastRequest()) &&
                getLastAPIFiltersUsed().equals(userIntel.getLastAPIFiltersUsed()) &&
                getLastNonAPIFiltersUsed().equals(userIntel.getLastNonAPIFiltersUsed()) &&
                getFavorites().equals(userIntel.getFavorites());
    }

}
