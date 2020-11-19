package app.arxivorg.model;

import app.arxivorg.controller.DownloadOption;
import app.arxivorg.model.download.MockDownloadHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/*The articles used for the test may have had they categories and publication dates changed for the purpose
of the test : they may not fit the actual intel on the pdf file created.
 */


public class DownloadHandlerTest {
    private final Path testFilePath = Paths.get(MockDownloadHandler.getFilesDirectory());
    private final List<Article> articles = AtomParser.parse(Paths.get(testFilePath + File.separator + "DownloadFileTest.xml"));
    private String pathOfWorkingFolder;
    private final List<String> downloadLinksExpected = new ArrayList<>();
    private File fileArticle1, fileArticle2, fileArticle3, fileArticle4, fileArticle5, fileArticle6, fileFakeArticle;

    @BeforeEach
    public void createWorkingDirectory() {
        String builtPath = testFilePath.toAbsolutePath().toString();
        builtPath += File.separator + "tmp";
        assert new File(builtPath).mkdir() : "the test file is not created as expected";
        this.pathOfWorkingFolder = builtPath;
    }

    @BeforeEach
    public void generateListOfExpectedUrl() {
        downloadLinksExpected.add("https://arxiv.org/pdf/1911.11405v1");
        downloadLinksExpected.add("https://arxiv.org/pdf/1907.06744v1");
        downloadLinksExpected.add("https://arxiv.org/pdf/1409.1340v1");
        downloadLinksExpected.add("https://arxiv.org/pdf/1210.7708v1");
        downloadLinksExpected.add("https://arxiv.org/pdf/1612.01842v2");
        downloadLinksExpected.add("https://arxiv.org/pdf/0705.3599v1");
        downloadLinksExpected.add("https://badDownloadLink");
    }

    @Test
    public void testDownloadInSameFolder(){
        List<String> downloadLinksGenerated = new ArrayList<>();
        List<Article> failedDownload =
                MockDownloadHandler.download(articles, pathOfWorkingFolder, DownloadOption.SAME_FOLDER, downloadLinksGenerated);

        initializeExpectedFiles(DownloadOption.SAME_FOLDER);
        launchAssertionsTest(downloadLinksGenerated, failedDownload, 0);
    }

    @Test
    public void testDownloadHandlerWithCategoryOption() {
        List<String> downloadLinksGenerated = new ArrayList<>();
        List<Article> failedDownload =
                MockDownloadHandler.download(articles, pathOfWorkingFolder,
                        DownloadOption.CATEGORY_ONLY, downloadLinksGenerated);

        initializeExpectedFiles(DownloadOption.CATEGORY_ONLY);

        launchAssertionsTest(downloadLinksGenerated, failedDownload, 4);
    }

    @Test
    public void testDownloadHandlerWithPublicationDateOption() {
        List<String> downloadLinksGenerated = new ArrayList<>();
        List<Article> failedDownload =
                MockDownloadHandler.download(articles, pathOfWorkingFolder,
                    DownloadOption.PUBLICATION_DATE_ONLY, downloadLinksGenerated);

        initializeExpectedFiles(DownloadOption.PUBLICATION_DATE_ONLY);

        launchAssertionsTest(downloadLinksGenerated, failedDownload, 4);
    }

    @Test
    public void testDownloadHandlerWithPublicationThenCategoryOption() {
        List<String> downloadLinksGenerated = new ArrayList<>();
        List<Article> failedDownload =
                MockDownloadHandler.download(articles, pathOfWorkingFolder,
                    DownloadOption.PUBLICATION_DATE_THEN_CATEGORY, downloadLinksGenerated);

        initializeExpectedFiles(DownloadOption.PUBLICATION_DATE_THEN_CATEGORY);

        launchAssertionsTest(downloadLinksGenerated, failedDownload, 9);
    }

    @Test
    public void testDownloadHandlerWithCategoryThenPublicationOption() {
        List<String> downloadLinksGenerated = new ArrayList<>();
        List<Article> failedDownload =
                MockDownloadHandler.download(articles, pathOfWorkingFolder,
                        DownloadOption.CATEGORY_THEN_PUBLICATION_DATE, downloadLinksGenerated);

        initializeExpectedFiles(DownloadOption.CATEGORY_THEN_PUBLICATION_DATE);

        launchAssertionsTest(downloadLinksGenerated, failedDownload, 9);
    }

    @AfterEach
    public void deleteWorkingDirectory() {
        File folder = new File(pathOfWorkingFolder);

        File[] contents = folder.listFiles();

        if (contents != null) {
            for (File fileIncluded : contents) {
                if (!Files.isSymbolicLink(fileIncluded.toPath())) {
                    deleteWorkingDirectory(fileIncluded);
                }
            }
        }
        assert folder.delete() : "the test folder and the files copied are not deleted";
    }

    private void deleteWorkingDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File fileIncluded : contents) {
                if (!Files.isSymbolicLink(fileIncluded.toPath())) {
                    deleteWorkingDirectory(fileIncluded);
                }
            }
        }
        assert file.delete() : "the test folder and the files copied are not deleted";
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

        assertEquals(numberOfFolderExpected, getNumberOfFolders(pathOfWorkingFolder));

        assertEquals(downloadLinksExpected, downloadLinksGenerated);
    }

    private void initializeExpectedFiles(DownloadOption option) {
        String articlePathToString = pathOfWorkingFolder + File.separator;
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

}
