package app.arxivorg.model.download;

import app.arxivorg.controller.DownloadOption;
import app.arxivorg.model.Article;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;


//This class is to use only for tests. It copies the methods of DownloadHandler, but copying test articles
// instead of downloading them. Report to DownloadHandler class for the missing javadoc.
public class MockDownloadHandler extends DownloadHandler {

    private final static String filesDirectory = "src/test/java/app/arxivorg/model/resources";

    /**
     * Copy the articles in parameter starting at the absolute path specified. Depending of the option chosen,
     * creates the tree view of folders where to copy each article in, starting at the absolute path
     * given in parameter.
     * If a tree view is generated, and in case of a copy error, the folder created is deleted if empty.
     *
     * Because the arxiv download links are in http and need to be in https, the url generated list harvest them so
     * the switchFromHTTPToHTTPS method can be tested using this mock.
     *
     * @param articles : the list of articles to copy.
     * @param pathToString : a string representation of the folder absolute path where to copy the articles.
     * @param option : the download option picked in the enum DownloadOption.
     * @param urlGenerated : the list of the url generated through the method.
     * @return a list of all the articles that were not copied due to errors.
     */

    public static List<Article> download (List<Article> articles, String pathToString, DownloadOption option,
                                          List<String> urlGenerated) {
        String finalPath;
        List<Article> failedToDownloadArticles = new ArrayList<>();

        switch (option) {

            case SAME_FOLDER:
                new File(pathToString).mkdirs();

                for (Article article : articles) {
                    Path target = Paths.get(pathToString + File.separator + getFileName(article));
                    urlGenerated.add(switchFromHttpToHttps(article.getDownloadLink()));
                    if(!getArticle(getSourcePath(article), target)) {
                        failedToDownloadArticles.add(article);
                    }
                }
                break;

            case CATEGORY_ONLY:
                for (Article article : articles) {
                    finalPath =
                            buildDirectoryPathWithOption(article, pathToString, DownloadOption.CATEGORY_ONLY);
                    urlGenerated.add(switchFromHttpToHttps(article.getDownloadLink()));
                    if(!downloadToPath(article, finalPath)) {
                        failedToDownloadArticles.add(article);
                        new File(finalPath).delete(); //only deletes if the folder is empty.
                    }
                }
                break;

            case PUBLICATION_DATE_ONLY:
                for (Article article : articles) {
                    finalPath =
                            buildDirectoryPathWithOption(article, pathToString, DownloadOption.PUBLICATION_DATE_ONLY);
                    urlGenerated.add(switchFromHttpToHttps(article.getDownloadLink()));
                    if(!downloadToPath(article, finalPath)) {
                        failedToDownloadArticles.add(article);
                        new File(finalPath).delete(); //only deletes if the folder is empty.
                    }
                }
                break;


            case CATEGORY_THEN_PUBLICATION_DATE:
                for (Article article : articles) {
                    finalPath =
                            buildDirectoryPathWithOption(article, pathToString, DownloadOption.CATEGORY_THEN_PUBLICATION_DATE);
                    urlGenerated.add(switchFromHttpToHttps(article.getDownloadLink()));
                    if(!downloadToPath(article, finalPath)) {
                        failedToDownloadArticles.add(article);
                        deleteDirectoryAndCreatedParentIfEmpty(new File(finalPath));
                    }
                }
                break;

            case PUBLICATION_DATE_THEN_CATEGORY:
                for (Article article : articles) {
                    finalPath =
                            buildDirectoryPathWithOption(article, pathToString, DownloadOption.PUBLICATION_DATE_THEN_CATEGORY);
                    urlGenerated.add(switchFromHttpToHttps(article.getDownloadLink()));
                    if(!downloadToPath(article, finalPath)) {
                        failedToDownloadArticles.add(article);
                        deleteDirectoryAndCreatedParentIfEmpty(new File(finalPath));
                    }
                }
                break;

            default:
                new IllegalArgumentException("This option is not handled " + option).printStackTrace();
                break;
        }

        return failedToDownloadArticles;

    }

    private static boolean downloadToPath(Article article, String directoryPathToString) {
        new File(directoryPathToString).mkdirs();
        String fileName = getFileName(article);
        Path target = Paths.get(directoryPathToString + File.separator + fileName);
        return getArticle(getSourcePath(article), target);
    }

    private static boolean getArticle(Path source, Path target) {
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static Path getSourcePath(Article article) {
        String finalPath = filesDirectory + File.separator + getFileName(article);
        System.out.println(finalPath);
        return Paths.get(finalPath);
    }

    public static String getFilesDirectory() {
        return filesDirectory;
    }

}
