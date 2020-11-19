package app.arxivorg.model.userintelhandler;

import app.arxivorg.model.Article;
import app.arxivorg.model.AtomParser;
import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.apifilters.FilterByAuthors;
import app.arxivorg.model.filters.apifilters.FilterByCategories;
import app.arxivorg.model.filters.apifilters.FilterByTitle;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntelTest {

    private final List<Article> initialArticles = AtomParser.parse(
            Paths.get("src/test/java/app/arxivorg/model/resources/TestFile.atom.xml"));
    private final List<APIFilter> initialAPIFilters = new ArrayList<>();
    private final List<NonAPIFilter> initialNonAPIFilters = new ArrayList<>();
    private final LocalDate date = LocalDate.of(2020, 4, 12);
    private UserIntel userIntel;

    @BeforeEach
    public void createUserIntel() {
        APIFilter filterCat = new FilterByCategories("cat1, cat2");
        APIFilter filterAut = new FilterByAuthors("Bernard Levure, Saucisson");

        this.initialAPIFilters.add(filterCat);
        this.initialAPIFilters.add(filterAut);

        this.initialNonAPIFilters.add(new FilterByDate(LocalDate.of(2020, 3, 27)));

        assert initialArticles != null;
        List<Article> articles = new ArrayList<>(initialArticles);
        List<APIFilter> apiFilters = new ArrayList<>(initialAPIFilters);

        List<NonAPIFilter> nonAPIFilters = new ArrayList<>(initialNonAPIFilters);

        LocalDate dateForUserIntel = LocalDate.from(date);

        this.userIntel = new UserIntel(dateForUserIntel, apiFilters, nonAPIFilters, articles);
    }

    @Test
    public void testGetLastUsageDate() {
        assertTrue(userIntel.getDateOfLastRequest().isEqual(date));
    }

    @Test
    public void testGetLastAPIFiltersUsed() {
        assertEquals(initialAPIFilters, userIntel.getLastAPIFiltersUsed());
    }

    @Test
    public void testGetLastNonAPIFiltersUsed() {
        assertEquals(initialNonAPIFilters, userIntel.getLastNonAPIFiltersUsed());
    }

    @Test
    public void testGetFavorites() {
        assertEquals(initialArticles, userIntel.getFavorites());
    }


    @Test
    public void testAddFavorite() {
        Article newArticle = new Article("f", date, "f", "f", "f", "f",
                new ArrayList<>(), new ArrayList<>());

        userIntel.addFavorite(newArticle);

        assertEquals((initialArticles.size() + 1), userIntel.getFavorites().size());
        assertTrue(userIntel.getFavorites().contains(newArticle));
        assertFalse(userIntel.addFavorite((newArticle)));
    }

    @Test
    public void testRemoveFavorite() {
        Article articleToRemove = initialArticles.get(0);

        userIntel.removeFavorite(articleToRemove);
        assertEquals(initialArticles.size() - 1, userIntel.getFavorites().size());
        assertFalse(userIntel.getFavorites().contains(articleToRemove));
    }

    @Test
    public void testUpdateFiltersUsed() {
        List<APIFilter> updatedAPIFilters = new ArrayList<>();
        updatedAPIFilters.add(new FilterByTitle("HALP"));

        List<NonAPIFilter> updatedNonAPIFilters = new ArrayList<>();
        updatedNonAPIFilters.add(new FilterByDate(LocalDate.of(2020, 5, 12)));

        userIntel.updateFiltersUsed(updatedAPIFilters, initialNonAPIFilters);
        assertEquals(updatedAPIFilters, userIntel.getLastAPIFiltersUsed());
        assertEquals(initialNonAPIFilters, userIntel.getLastNonAPIFiltersUsed());

        userIntel.updateFiltersUsed(initialAPIFilters, updatedNonAPIFilters);
        assertEquals(updatedNonAPIFilters, userIntel.getLastNonAPIFiltersUsed());
        assertEquals(initialAPIFilters, userIntel.getLastAPIFiltersUsed());

        userIntel.updateFiltersUsed(updatedAPIFilters, updatedNonAPIFilters);
        assertEquals(updatedAPIFilters, userIntel.getLastAPIFiltersUsed());
        assertEquals(updatedNonAPIFilters, userIntel.getLastNonAPIFiltersUsed());
    }

    @Test
    public void testUpdateDateOfLastUsage() {
        LocalDate newDate = LocalDate.of(2019, 10, 2);

        userIntel.updateDateOfLastUsage(newDate);

        assertTrue(userIntel.getDateOfLastRequest().isEqual(newDate));
    }


    @Test
    public void testEquals() {
        Article newArticle = new Article("f", date, "f", "f", "f", "f",
                new ArrayList<>(), new ArrayList<>());

        assert initialArticles != null;
        List<Article> articles = new ArrayList<>(initialArticles);
        List<APIFilter> apiFilters = new ArrayList<>(initialAPIFilters);
        List<NonAPIFilter> nonAPIFilters = new ArrayList<>(initialNonAPIFilters);
        LocalDate dateForUserIntel = LocalDate.from(date);
        UserIntel userIntelCopy = new UserIntel(dateForUserIntel, apiFilters, nonAPIFilters, articles);

        assertEquals(userIntelCopy, userIntel);

        userIntelCopy.updateDateOfLastUsage(LocalDate.now());
        assertNotEquals(userIntelCopy, userIntel);
        userIntelCopy.updateDateOfLastUsage(LocalDate.from(date));

        userIntelCopy.addFavorite(newArticle);
        assertNotEquals(userIntelCopy, userIntel);
        userIntelCopy.removeFavorite(newArticle);
        assertEquals(userIntelCopy, userIntel);

        List<APIFilter> updatedAPIFilters = new ArrayList<>();
        updatedAPIFilters.add(new FilterByCategories("cat2, cat3"));
        updatedAPIFilters.add(new FilterByAuthors("Saucisson, Benard Levure"));

        List<NonAPIFilter> updatedNonAPIFilters = new ArrayList<>();
        updatedNonAPIFilters.add(new FilterByDate(LocalDate.of(2020, 5, 12)));

        userIntelCopy.updateFiltersUsed(updatedAPIFilters, nonAPIFilters);
        assertNotEquals(userIntelCopy, userIntel);
        userIntelCopy.updateFiltersUsed(apiFilters, nonAPIFilters);
        assertEquals(userIntelCopy, userIntel);

        userIntelCopy.updateFiltersUsed(apiFilters, updatedNonAPIFilters);
        assertNotEquals(userIntelCopy, userIntel);
        userIntelCopy.updateFiltersUsed(apiFilters, nonAPIFilters);
        assertEquals(userIntelCopy, userIntel);
    }

}
