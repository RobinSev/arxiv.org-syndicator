package app.arxivorg.model.download;

import app.arxivorg.controller.DownloadOption;
import app.arxivorg.model.Article;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class DownloadHandler {

    /**
     * Download the articles in parameter starting at the absolute path specified. Depending of the option chosen,
     * creates the tree view of folders where to download each article in, starting at the absolute path
     * given in parameter.
     * If a tree view is generated, and in case of a download error, the folder created is deleted if empty.
     *
     * @param articles : the list of articles to download.
     * @param pathToString : a string representation of the folder absolute path where to download the articles.
     * @param option : the download option picked in the enum DownloadOption.
     * @return a list of all the articles that were not successfully downloaded due to errors.
     */
    public static List<Article> download (List<Article> articles, String pathToString, DownloadOption option) {
        String finalPath;
        List<Article> failedToDownloadArticles = new ArrayList<>();


        switch (option) {

            case SAME_FOLDER:
                new File(pathToString).mkdirs();

                for (Article article : articles) {
                    String fileName = getFileName(article);
                    Path targetPath = Paths.get(pathToString + File.separator + fileName);
                    String downloadLinkToHttps = switchFromHttpToHttps(article.getDownloadLink());
                    if(!downloadArticle(downloadLinkToHttps, targetPath)) {
                        failedToDownloadArticles.add(article);
                    }
                }
                break;

            case CATEGORY_ONLY:
                for (Article article : articles) {
                    finalPath =
                            buildDirectoryPathWithOption(article, pathToString, DownloadOption.CATEGORY_ONLY);
                    if (!downloadToPath(article, finalPath)) {
                        failedToDownloadArticles.add(article);
                        new File(finalPath).delete(); //only deletes if the folder is empty.
                    }
                }
                break;

            case PUBLICATION_DATE_ONLY:
                for (Article article : articles) {
                    finalPath =
                            buildDirectoryPathWithOption(article, pathToString, DownloadOption.PUBLICATION_DATE_ONLY);
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

    /**
     * Determine the folder absolute path where to download the specified article depending of the option chosen.
     * Used only if the option requires a tree view to be generated.
     *
     * @param article : the article to create (or select) a folder for.
     * @param pathToString : a string representation of the folder absolute path where to start the tree view.
     * @param option : the download option picked in the enum DownloadOption.
     * @return a String representation of the specific folder absolute path where to download the article
     *         given in parameter.
     */
    protected static String buildDirectoryPathWithOption(Article article, String pathToString, DownloadOption option) {
        String finalPath = pathToString;

        switch (option) {

            case CATEGORY_ONLY:
                return finalPath + File.separator + article.getPrimaryCategory();

            case PUBLICATION_DATE_ONLY:
                return finalPath + File.separator + article.getPublicationDate().toString();

            case CATEGORY_THEN_PUBLICATION_DATE:
                finalPath += File.separator + article.getPrimaryCategory();
                finalPath += File.separator + article.getPublicationDate().toString();
                return finalPath;

            case PUBLICATION_DATE_THEN_CATEGORY:
                finalPath += File.separator + article.getPublicationDate().toString();
                finalPath += File.separator + article.getPrimaryCategory();
                return finalPath;

            default:
                return pathToString;
        }
    }


    /**
     * Download the article in parameter in the absolute path specified. If the folder doesn't exist, it creates it.
     * Complete the pathToString given in parameter with the file name.
     *
     * @param article : the article to download.
     * @param directoryPathToString : a string representation of the folder absolute path where to download the article.
     * @return {@code true} if the download is successful. {@code false} otherwise;
     */
    private static boolean downloadToPath(Article article, String directoryPathToString) {
        new File(directoryPathToString).mkdirs();
        String fileName = getFileName(article);
        Path target = Paths.get(directoryPathToString + File.separator + fileName);
        String downloadLinkToHttps = switchFromHttpToHttps(article.getDownloadLink());
        return downloadArticle(downloadLinkToHttps, target);
    }


    /**
     * Download a single article to a specified target.
     *
     * @param downloadLink : the String representation of the link to use for download.
     * @param target : the absolute path of the target file where to download the article in.
     * @return {@code true} if the download is successful. {@code false} otherwise;
     */
    private static boolean downloadArticle(String downloadLink, Path target) {
        try {
            URL url = new URL(downloadLink);
            InputStream inputStream = url.openStream();
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return false;
        }

        return true;
    }


    /**
     * Generate a file name for the article to download, using the specific id on the article. This id, which is the
     * digits in parameter of the arxiv link, is extracted in this method.
     * All articles are downloaded in pdf format.
     *
     * @param article : the article to generate a file name for.
     * @return a String representation of the file name.
     */
    protected static String getFileName(Article article) {
        String fileName = article.getArxivLink().substring(article.getArxivLink().lastIndexOf('/') + 1);
        return fileName + ".pdf";
    }

    protected static String switchFromHttpToHttps(String request) {
        return request.contains("https") ? request : request.replaceFirst("http", "https");
    }

    protected static void deleteDirectoryAndCreatedParentIfEmpty(File file) {
        file.delete();
        file.getParentFile().delete();
    }

}
