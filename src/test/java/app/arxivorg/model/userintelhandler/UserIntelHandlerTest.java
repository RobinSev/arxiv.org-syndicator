package app.arxivorg.model.userintelhandler;

import app.arxivorg.model.Article;
import app.arxivorg.model.AtomParser;
import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import app.arxivorg.model.filters.apifilters.FilterByAuthors;
import app.arxivorg.model.filters.apifilters.FilterByCategories;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserIntelHandlerTest {

    private static final String fileTestPath = "src/test/java/app/arxivorg/model/resources/userIntelHandlerFileTest.json";
    private final List<Article> initialArticles = AtomParser.parse(
            Paths.get("src/test/java/app/arxivorg/model/resources/TestFile.atom.xml"));
    private List<APIFilter> initialAPIFilters;
    private final LocalDate date = LocalDate.of(2020, 4, 12);
    private List<NonAPIFilter> initialNonAPIFilters;
    private UserIntel userIntelToInitialState;
    private UserIntel userIntelForUsage;
    private final UserIntelHandler userIntelHandler = new UserIntelHandler(fileTestPath);
    private final GsonBuilder gson = new GsonBuilder().registerTypeAdapter(APIFilter.class, new APIFilterClassAdapter())
                                                .registerTypeAdapter(NonAPIFilter.class, new NonAPIFilterClassAdapter());


    @BeforeAll
    static void createEmptyTestFile(){
        File file = new File(fileTestPath);
        try {
            assert file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void createUserIntel() {
        APIFilter filterCat = new FilterByCategories("cat1, cat2");
        APIFilter filterAut = new FilterByAuthors("Bernard Levure, Saucisson");

        this.initialAPIFilters = new ArrayList<>();
        this.initialAPIFilters.add(filterCat);
        this.initialAPIFilters.add(filterAut);

        this.initialNonAPIFilters = new ArrayList<>();
        initialNonAPIFilters.add(new FilterByDate(LocalDate.of(2019, 3, 14)));

        assert initialArticles != null;
        List<Article> articles = new ArrayList<>(initialArticles);
        List<APIFilter> apiFilters = new ArrayList<>(initialAPIFilters);
        List<NonAPIFilter> nonAPIFilters = new ArrayList<>(initialNonAPIFilters);

        LocalDate dateForUserIntel = LocalDate.from(date);
        this.userIntelForUsage = new UserIntel(dateForUserIntel, apiFilters, nonAPIFilters, articles);
        this.userIntelToInitialState = new UserIntel(dateForUserIntel, apiFilters, nonAPIFilters, articles);
    }

    @BeforeEach
    public void resetTestFile() throws IOException {
        String userIntelToJson = gson.create().toJson(userIntelToInitialState);
        overwriteTestFile(userIntelToJson);
        assert userIntelToInitialState.equals(userIntelHandler.getUserIntel());
    }

    @Test
    public void testGetUserIntel() throws IOException {
        overwriteTestFile("");

        UserIntel emptyUserIntel = new UserIntel(LocalDate.MIN, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        assertEquals(emptyUserIntel, userIntelHandler.getUserIntel());

        String userIntelToJson = gson.create().toJson(userIntelForUsage);
        overwriteTestFile(userIntelToJson);

        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());
        assertNotEquals(emptyUserIntel, userIntelHandler.getUserIntel());
    }

    @Test
    public void testAddFavorite() throws Exception {
        Article newArticle = new Article("f", date, "f", "f", "f", "f",
                new ArrayList<>(), new ArrayList<>());

        assertTrue(userIntelForUsage.addFavorite(newArticle));
        assertNotEquals(userIntelForUsage, userIntelHandler.getUserIntel());

        assertTrue(userIntelHandler.addFavorite(newArticle));
        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());

        assertFalse(userIntelHandler.addFavorite(newArticle));
        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());
    }

    @Test
    public void testRemoveFavorite() throws Exception {
        assert initialArticles != null;
        Article articleToRemove = initialArticles.get(0);

        assertTrue(userIntelForUsage.removeFavorite(articleToRemove));
        assertNotEquals(userIntelForUsage, userIntelHandler.getUserIntel());

        assertTrue(userIntelHandler.removeFavorite(articleToRemove));
        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());
    }


    @Test
    public void testUpdateFiltersAndDateOfLastRequest() throws Exception {
        List<APIFilter> updatedAPIFilters = new ArrayList<>();
        updatedAPIFilters.add(new FilterByAuthors("Henry Bigleux, Stéphanie SENDHALP"));

        List<NonAPIFilter> updatedNonApiFilters = new ArrayList<>();
        updatedNonApiFilters.add(new FilterByDate(LocalDate.of(2010, 9, 2)));

        userIntelForUsage.updateDateOfLastUsage(LocalDate.now());
        assertNotEquals(userIntelForUsage, userIntelHandler.getUserIntel());
        userIntelForUsage.updateDateOfLastUsage(LocalDate.from(date));
        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());

        userIntelForUsage.updateFiltersUsed(updatedAPIFilters, initialNonAPIFilters);
        assertNotEquals(userIntelForUsage, userIntelHandler.getUserIntel());
        userIntelForUsage.updateFiltersUsed(initialAPIFilters, initialNonAPIFilters);
        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());

        userIntelForUsage.updateFiltersUsed(initialAPIFilters, updatedNonApiFilters);
        assertNotEquals(userIntelForUsage, userIntelHandler.getUserIntel());


        userIntelHandler.updateFiltersAndDateOfLastRequest(updatedAPIFilters, updatedNonApiFilters);

        userIntelForUsage.updateFiltersUsed(updatedAPIFilters, updatedNonApiFilters);
        userIntelForUsage.updateDateOfLastUsage(LocalDate.now());
        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());

    }

    @Test
    public void testUpdateOnlyDateOfLastRequest() throws IOException {
        userIntelForUsage.updateDateOfLastUsage(LocalDate.now());
        assertNotEquals(userIntelForUsage, userIntelHandler.getUserIntel());
        userIntelForUsage.updateDateOfLastUsage(LocalDate.from(date));
        assertEquals(userIntelForUsage, userIntelHandler.getUserIntel());

        UserIntel userIntelWithUpdatedDateOnly = userIntelHandler.getUserIntel();
        assertEquals(userIntelToInitialState.getFavorites(), userIntelWithUpdatedDateOnly.getFavorites());
        assertEquals(userIntelToInitialState.getLastAPIFiltersUsed(), userIntelWithUpdatedDateOnly.getLastAPIFiltersUsed());
        assertEquals(userIntelToInitialState.getLastNonAPIFiltersUsed(), userIntelWithUpdatedDateOnly.getLastNonAPIFiltersUsed());
    }

    @AfterAll
    static void deleteTestFile() {
        File file = new File(fileTestPath);
        assert file.delete();
    }

    private void overwriteTestFile(String content) {
        try {
            Files.writeString(Paths.get(fileTestPath), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
