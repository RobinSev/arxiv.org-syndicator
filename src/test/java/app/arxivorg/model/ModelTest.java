package app.arxivorg.model;

import app.arxivorg.controller.DownloadOption;
import app.arxivorg.model.download.MockDownloadHandler;
import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.apifilters.FilterByAuthors;
import app.arxivorg.model.filters.apifilters.FilterByCategories;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import app.arxivorg.model.userintelhandler.APIFilterClassAdapter;
import app.arxivorg.model.userintelhandler.NonAPIFilterClassAdapter;
import app.arxivorg.model.userintelhandler.UserIntel;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    private final Model model = new Model();
    private static final String fileTestPath = "src/test/java/app/arxivorg/model/resources/userIntelHandlerFileTest.json";
    private final List<Article> initialArticlesForUserIntel = AtomParser.parse(
            Paths.get("src/test/java/app/arxivorg/model/resources/TestFile.atom.xml"));
    private List<APIFilter> initialAPIFilters;
    private final LocalDate date = LocalDate.of(2000, 1, 12);
    private List<NonAPIFilter> initialNonAPIFilters;
    private UserIntel userIntelToInitialState;
    private UserIntel userIntelForUsage;
    private final GsonBuilder gson = new GsonBuilder().registerTypeAdapter(APIFilter.class, new APIFilterClassAdapter())
            .registerTypeAdapter(NonAPIFilter.class, new NonAPIFilterClassAdapter());

    private final Path sourceArticlesForDownload = Paths.get(MockDownloadHandler.getFilesDirectory());
    private final List<Article> articlesForDownload = AtomParser.parse(
            Paths.get(sourceArticlesForDownload + File.separator + "DownloadFileTest.xml"));
    private static String targetPathForDownload;
    private final List<String> downloadLinksExpected = new ArrayList<>();
    private File fileArticle1, fileArticle2, fileArticle3, fileArticle4, fileArticle5, fileArticle6, fileFakeArticle;
    private final String originalPathOfUserIntelHandler = model.getUserIntelHandlerFilePath();


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
        public void setUserIntelHandlerForTest() {
            model.setTestUserIntelHandlerPath(fileTestPath);
        }

        @BeforeEach
        public void createUserIntel() {
            APIFilter filterCat = new FilterByCategories("cs.CL, cs.LG");
            APIFilter filterAut = new FilterByAuthors("Robin Harper");

            this.initialAPIFilters = new ArrayList<>();
            this.initialAPIFilters.add(filterCat);
            this.initialAPIFilters.add(filterAut);

            this.initialNonAPIFilters = new ArrayList<>();
            initialNonAPIFilters.add(new FilterByDate(LocalDate.of(2005, 1, 12)));

            assert initialArticlesForUserIntel != null;
            List<Article> articles = new ArrayList<>(initialArticlesForUserIntel);
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
            assert userIntelToInitialState.equals(model.getUserIntel());
        }

        @Test
        public void testGetUserIntel() throws IOException {
            overwriteTestFile("");

            UserIntel emptyUserIntel = new UserIntel(LocalDate.MIN, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            assertEquals(emptyUserIntel, model.getUserIntel());

            String userIntelToJson = gson.create().toJson(userIntelForUsage);
            overwriteTestFile(userIntelToJson);

            assertEquals(userIntelForUsage, model.getUserIntel());
            assertNotEquals(emptyUserIntel, model.getUserIntel());
        }


        @Test
        public void testRequestArticles() throws Exception {
            List<APIFilter> apiFilters = new ArrayList<>();
            apiFilters.add(new FilterByCategories("cs.CL, cs.LG"));

            List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
            nonAPIFilters.add(new FilterByDate(LocalDate.of(2005, 1, 12)));

            List<Article> resultsWithFilters = model.requestArticles(apiFilters, nonAPIFilters, 10);
            assertFalse(resultsWithFilters.isEmpty());
            assertEquals(10, resultsWithFilters.size());
            for (Article article : resultsWithFilters) {
                assertTrue(article.getPublicationDate().isAfter(date));
                assertTrue(article.getCategories().contains("cs.CL"));
                assertTrue(article.getCategories().contains("cs.LG"));
            }
            
            
            UserIntel savedData = model.getUserIntel();
            assertEquals(apiFilters, savedData.getLastAPIFiltersUsed());
            assertEquals(nonAPIFilters, savedData.getLastNonAPIFiltersUsed());
            assertEquals(LocalDate.now(), savedData.getDateOfLastRequest());
        

            List<Article> resultsWithNoFilters = model.requestArticles(new ArrayList<>(), new ArrayList<>(), 20);
            assertEquals(20, resultsWithNoFilters.size());


            savedData = model.getUserIntel();
            assertTrue(savedData.getLastAPIFiltersUsed().isEmpty());
            assertTrue(savedData.getLastNonAPIFiltersUsed().isEmpty());

            apiFilters.clear();
            apiFilters.add(new FilterByCategories("cs.CL"));
            apiFilters.add(new FilterByAuthors("Robin Harper"));
            List<Article> noResultExpected = model.requestArticles(apiFilters, new ArrayList<>(), 50);
            assertTrue(noResultExpected.isEmpty());


            List<Article> nullResult = model.requestArticles(apiFilters, nonAPIFilters, 2001);
            assertNull(nullResult);
            nullResult = model.requestArticles(apiFilters, nonAPIFilters, 0);
            assertNull(nullResult);
        }

        @Test
        public void testRequestMoreArticles() throws Exception {
            List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
            nonAPIFilters.add(new FilterByDate(date));

            ArrayList<APIFilter> apiFilters = new ArrayList<>();
            apiFilters.add(new FilterByCategories("cs.CL"));

            List<Article> references = model.requestArticles(apiFilters, nonAPIFilters, 10);
            assertEquals(10, references.size());
            for (Article article : references) {
                assertTrue(article.getPublicationDate().isAfter(date));
                assertTrue(article.getCategories().contains("cs.CL"));
            }


            List<Article> addedArticles = model.requestMoreArticles(references);
            assertEquals(10, addedArticles.size());
            for (Article article : addedArticles) {
                assertTrue(article.getPublicationDate().isAfter(date));
                assertTrue(article.getCategories().contains("cs.CL"));
                assertFalse(references.contains(article));
            }
            

            List<Article> secondWave = model.requestMoreArticles(addedArticles);
            assertEquals(10, secondWave.size());
            for (Article article : secondWave) {
                assertTrue(article.getPublicationDate().isAfter(date));
                assertTrue(article.getCategories().contains("cs.CL"));
                assertFalse(references.contains(article));
                assertFalse(addedArticles.contains(article));
            }

            UserIntel intelSaved = model.getUserIntel();
            assertEquals(apiFilters, intelSaved.getLastAPIFiltersUsed());
            assertEquals(nonAPIFilters, intelSaved.getLastNonAPIFiltersUsed());
            assertEquals(LocalDate.now(), intelSaved.getDateOfLastRequest());

        }

        @Test
        public void testAddFavorite() throws Exception {
            Article newArticle = new Article("f", date, "f", "f", "f", "f",
                    new ArrayList<>(), new ArrayList<>());

            assertEquals(userIntelForUsage, model.getUserIntel());

            assertTrue(model.addFavorite(newArticle));
            assertNotEquals(userIntelForUsage, model.getUserIntel());

            assertFalse(model.addFavorite(newArticle));
        }

        @Test
        public void testRemoveFavorite() throws Exception {
            assert initialArticlesForUserIntel != null;
            Article articleToRemove = initialArticlesForUserIntel.get(0);

            assertTrue(model.removeFavorite(articleToRemove));
            assertNotEquals(userIntelForUsage, model.getUserIntel());
        }


        @Test
        public void testDownload() {
            generateListOfExpectedUrl();

            createDirectoryTargetForDownload();
            List<String> downloadLinksGenerated = new ArrayList<>();
            List<Article> failedDownload =
                    MockDownloadHandler.download(articlesForDownload, targetPathForDownload,
                            DownloadOption.SAME_FOLDER, downloadLinksGenerated);
            initializeExpectedFiles(DownloadOption.SAME_FOLDER);

            launchAssertionsTest(downloadLinksGenerated, failedDownload, 0);
            deleteTargetDirectoryForDownload();


            createDirectoryTargetForDownload();
            downloadLinksGenerated = new ArrayList<>();
            failedDownload =
                    MockDownloadHandler.download(articlesForDownload, targetPathForDownload,
                            DownloadOption.CATEGORY_ONLY, downloadLinksGenerated);

            initializeExpectedFiles(DownloadOption.CATEGORY_ONLY);
            launchAssertionsTest(downloadLinksGenerated, failedDownload, 4);
            deleteTargetDirectoryForDownload();


            createDirectoryTargetForDownload();
            downloadLinksGenerated = new ArrayList<>();
            failedDownload =
                    MockDownloadHandler.download(articlesForDownload, targetPathForDownload,
                            DownloadOption.PUBLICATION_DATE_ONLY, downloadLinksGenerated);

            initializeExpectedFiles(DownloadOption.PUBLICATION_DATE_ONLY);
            launchAssertionsTest(downloadLinksGenerated, failedDownload, 4);
            deleteTargetDirectoryForDownload();


            createDirectoryTargetForDownload();
            downloadLinksGenerated = new ArrayList<>();
            failedDownload =
                    MockDownloadHandler.download(articlesForDownload, targetPathForDownload,
                            DownloadOption.CATEGORY_THEN_PUBLICATION_DATE, downloadLinksGenerated);

            initializeExpectedFiles(DownloadOption.CATEGORY_THEN_PUBLICATION_DATE);
            launchAssertionsTest(downloadLinksGenerated, failedDownload, 9);
            deleteTargetDirectoryForDownload();


            createDirectoryTargetForDownload();
            downloadLinksGenerated = new ArrayList<>();
            failedDownload =
                    MockDownloadHandler.download(articlesForDownload, targetPathForDownload,
                            DownloadOption.PUBLICATION_DATE_THEN_CATEGORY, downloadLinksGenerated);

            initializeExpectedFiles(DownloadOption.PUBLICATION_DATE_THEN_CATEGORY);
            launchAssertionsTest(downloadLinksGenerated, failedDownload, 9);
            deleteTargetDirectoryForDownload();
        }

        @AfterEach
        public void resetUserIntelHandlerPathToOrigin() {
            model.setTestUserIntelHandlerPath(originalPathOfUserIntelHandler);
        }

        @AfterAll
        static void deleteTestFile() {
            File file = new File(fileTestPath);
            assert file.delete();
        }

        @AfterAll
        static void deleteTargetDirectoryForDownload() {
            File folder = new File(targetPathForDownload);

            File[] contents = folder.listFiles();

            if (contents != null) {
                for (File fileIncluded : contents) {
                    if (!Files.isSymbolicLink(fileIncluded.toPath())) {
                        deleteTargetDirectoryForDownload(fileIncluded);
                    }
                }
            }
            folder.delete();
        }


        private static void deleteTargetDirectoryForDownload(File file) {
            File[] contents = file.listFiles();
            if (contents != null) {
                for (File fileIncluded : contents) {
                    if (!Files.isSymbolicLink(fileIncluded.toPath())) {
                        deleteTargetDirectoryForDownload(fileIncluded);
                    }
                }
            }
            file.delete();
        }


        private void overwriteTestFile(String content) {
            try {
                Files.writeString(Paths.get(fileTestPath), content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

       private void createDirectoryTargetForDownload() {
            String builtPath = sourceArticlesForDownload.toAbsolutePath().toString();
            builtPath += File.separator + "tmp";
            assert new File(builtPath).mkdir() : "the test file is not created as expected";
            targetPathForDownload = builtPath;
        }

        private void generateListOfExpectedUrl() {
            downloadLinksExpected.add("https://arxiv.org/pdf/1911.11405v1");
            downloadLinksExpected.add("https://arxiv.org/pdf/1907.06744v1");
            downloadLinksExpected.add("https://arxiv.org/pdf/1409.1340v1");
            downloadLinksExpected.add("https://arxiv.org/pdf/1210.7708v1");
            downloadLinksExpected.add("https://arxiv.org/pdf/1612.01842v2");
            downloadLinksExpected.add("https://arxiv.org/pdf/0705.3599v1");
            downloadLinksExpected.add("https://badDownloadLink");
        }

        private void launchAssertionsTest(List<String> downloadLinksGenerated, List<Article> failedDownload,
                                          int numberOfFolderExpected) {
            assertTrue(fileArticle1.exists());
            assertTrue(fileArticle2.exists());
            assertTrue(fileArticle3.exists());
            assertTrue(fileArticle4.exists());
            assertTrue(fileArticle5.exists());
            assertTrue(fileArticle6.exists());
            assertFalse((fileFakeArticle.exists()));

            assert failedDownload != null;
            assertEquals(1, failedDownload.size());

            assertEquals(numberOfFolderExpected, getNumberOfFolders(targetPathForDownload));

            assertEquals(downloadLinksExpected, downloadLinksGenerated);
        }

        private int getNumberOfFolders(String path) {
            File file = new File(path);
            if (!file.isDirectory()) return -1;
            File[] allFiles = file.listFiles();
            if(allFiles == null) return -1;

            int numberOfFolders =0;

            for (File fileToEvaluate : allFiles) {
                if (fileToEvaluate.isDirectory())
                    numberOfFolders += getNumberOfFolders(fileToEvaluate.toString()) + 1;
            }
            return numberOfFolders;
        }

        private void initializeExpectedFiles(DownloadOption option) {
            String articlePathToString = targetPathForDownload + File.separator;
            String fileExpectedPathArticle1 = articlePathToString;
            String fileExpectedPathArticle2 = articlePathToString;
            String fileExpectedPathArticle3 = articlePathToString;
            String fileExpectedPathArticle4 = articlePathToString;
            String fileExpectedPathArticle5 = articlePathToString;
            String fileExpectedPathArticle6 = articlePathToString;
            String fakeFilePathForError = articlePathToString;

            switch (option) {
                case CATEGORY_ONLY:
                    fileExpectedPathArticle1 = articlePathToString + File.separator + "cs.CC" + File.separator;
                    fileExpectedPathArticle2 = articlePathToString + File.separator + "math.CO" + File.separator;
                    fileExpectedPathArticle3 = articlePathToString + File.separator + "math.DS" + File.separator;
                    fileExpectedPathArticle4 = articlePathToString + File.separator + "math.NA" + File.separator;
                    fileExpectedPathArticle5 = articlePathToString + File.separator + "math.NA" + File.separator;
                    fileExpectedPathArticle6 = articlePathToString + File.separator + "math.CO" + File.separator;
                    fakeFilePathForError = articlePathToString + File.separator + "ha.LP" + File.separator;
                    break;

                case PUBLICATION_DATE_ONLY:
                    fileExpectedPathArticle1 = articlePathToString + File.separator + "2019-07-15" + File.separator;
                    fileExpectedPathArticle2 = articlePathToString + File.separator + "2019-07-15" + File.separator;
                    fileExpectedPathArticle3 = articlePathToString + File.separator + "2019-07-14" + File.separator;
                    fileExpectedPathArticle4 = articlePathToString + File.separator + "2012-10-29" + File.separator;
                    fileExpectedPathArticle5 = articlePathToString + File.separator + "2012-10-29" + File.separator;
                    fileExpectedPathArticle6 = articlePathToString + File.separator + "2007-05-24" + File.separator;
                    fakeFilePathForError = articlePathToString + File.separator + "2007-05-23" + File.separator;
                    break;

                case PUBLICATION_DATE_THEN_CATEGORY:
                    fileExpectedPathArticle1 = articlePathToString + File.separator + "2019-07-15" + File.separator +
                            "cs.CC" + File.separator;
                    fileExpectedPathArticle2 = articlePathToString + File.separator + "2019-07-15" + File.separator +
                            "math.CO" + File.separator;
                    fileExpectedPathArticle3 = articlePathToString + File.separator + "2019-07-14" + File.separator +
                            "math.DS" + File.separator;
                    fileExpectedPathArticle4 = articlePathToString + File.separator + "2012-10-29" + File.separator +
                            "math.NA" + File.separator;
                    fileExpectedPathArticle5 = articlePathToString + File.separator + "2012-10-29" + File.separator +
                            "math.NA" + File.separator;
                    fileExpectedPathArticle6 = articlePathToString + File.separator + "2007-05-24" + File.separator +
                            "math.CO" + File.separator;
                    fakeFilePathForError = articlePathToString + File.separator + "2007-05-23" + File.separator +
                            "ha.LP" + File.separator;
                    break;

                case CATEGORY_THEN_PUBLICATION_DATE:
                    fileExpectedPathArticle1 = articlePathToString + File.separator + "cs.CC" + File.separator +
                            "2019-07-15" + File.separator;
                    fileExpectedPathArticle2 = articlePathToString + File.separator + "math.CO" + File.separator +
                            "2019-07-15" + File.separator;
                    fileExpectedPathArticle3 = articlePathToString + File.separator + "math.DS" + File.separator +
                            "2019-07-14" + File.separator;
                    fileExpectedPathArticle4 = articlePathToString + File.separator + "math.NA" + File.separator +
                            "2012-10-29" + File.separator;
                    fileExpectedPathArticle5 = articlePathToString + File.separator + "math.NA" + File.separator +
                            "2012-10-29" + File.separator;
                    fileExpectedPathArticle6 = articlePathToString + File.separator + "math.CO" + File.separator +
                            "2007-05-24" + File.separator;
                    fakeFilePathForError = articlePathToString + File.separator + "ha.LP" + File.separator +
                            "2007-05-23" + File.separator;

                case SAME_FOLDER:
                    break;
            }

            fileArticle1 = new File(fileExpectedPathArticle1 + "1911.11405v1.pdf");
            fileArticle2 = new File(fileExpectedPathArticle2 + "1907.06744v1.pdf");
            fileArticle3 = new File(fileExpectedPathArticle3 + "1409.1340v1.pdf");
            fileArticle4 = new File(fileExpectedPathArticle4 + "1210.7708v1.pdf");
            fileArticle5 = new File(fileExpectedPathArticle5 + "1612.01842v2.pdf");
            fileArticle6 = new File(fileExpectedPathArticle6 + "0705.3599v1.pdf");
            fileFakeArticle = new File(fakeFilePathForError + "notPresent.pdf");

        }


    List<Article> articles = AtomParser.parse(Paths.get("src/test/java/app/arxivorg/model/resources/TestFile.atom.xml"));

    @Test
    public void testStatisticsByCategory() {
        Map<String, Float> stat = model.statisticsByCategory(articles);
        double count = 0;
        for(Map.Entry<String,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertEquals(100, count, 0.0001);
    }

    @Test
    public void testStatisticsByAuthor() {
        Map<Author, Float> stat = model.statisticsByAuthor(articles);
        double count = 0;
        for(Map.Entry<Author,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertEquals(100, count, 0.0001);
    }

    @Test
    public void testStatisticsByDay() {
        Map<LocalDate, Float> stat = model.statisticsByNumberOfArticlePerDay(articles);
        double count = 0;
        for(Map.Entry<LocalDate,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertEquals(100, count, 0.0001);
    }


    @Test
    public void testStatisticsByExpressionInSummary() {
        ArrayList<String> expressions = new ArrayList<>();
        expressions.add("summary*|article");
        expressions.add("1.");

        Map<String, Float> stat = model.statisticsByExpressionInSummary(articles,expressions);
        double count = 0;
        for(Map.Entry<String,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertTrue(stat.containsKey("summary"));
        assertTrue(stat.containsKey("article"));
        assertTrue(stat.containsKey("1."));
        assertEquals(100, count, 0.0001);
    }

    @Test
    public void testStatisticsByExpressionInTitle() {
        ArrayList<String> expressions = new ArrayList<>();
        expressions.add("[at]|title");
        expressions.add("[12]|article");

        Map<String, Float> stat = model.statisticsByExpressionInTitle(articles,expressions);
        double count = 0;
        for(Map.Entry<String,Float> element : stat.entrySet()){
            count += element.getValue();
        }
        assertTrue(stat.containsKey("title"));
        assertTrue(stat.containsKey("article"));
        assertTrue(stat.containsKey("2"));
        assertEquals(100, count, 0.0001);
    }
}
