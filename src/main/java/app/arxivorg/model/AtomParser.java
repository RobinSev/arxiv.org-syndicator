package app.arxivorg.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AtomParser{

    /**
     * Parse the content of an atom file using the same construction as the ones given by the arxiv.org API.
     * Generate a list of Article corresponding to the content of the file.
     *
     * @param file : the path to the atom file to parse.
     * @return the list of articles corresponding to the articles on the atom file. {@code null} if the parse failed
     * with exception.
     */
    public static List<Article> parse(Path file) {
        try {
            String stringInAtomFormat = new String(Files.readAllBytes(file));
            return parse(stringInAtomFormat);
        } catch (IOException e) {
            e.printStackTrace(); //internal exception, no need to display anything through the user interface.
        }
        return null;
    }


    /**
     * Parse the content of a String in atom format, using the same construction as the ones given by the arxiv.org API.
     * Generate a list of Article corresponding to the content of the String.
     *
     * @param stringInAtomFormat : the String in atom format to parse.
     * @return the list of articles corresponding to the articles in the String.
     */
    public static List<Article> parse(String stringInAtomFormat) {

        String[] tabArticles = stringInAtomFormat.split("<entry>");
        List<Article> listArticles = new ArrayList<>();

        for(int i = 1 ; i < tabArticles.length ; i++){
            String articleOnString = tabArticles[i];

            String id = getId(articleOnString);
            LocalDate publicationDate = getPublicationDate(articleOnString);
            String title = getTitle(articleOnString);
            String summary = getSummary(articleOnString);
            String link = getDownloadLink(articleOnString);
            String comment = getComment(articleOnString);
            List<Author> author = getAuthors(articleOnString);
            List<String> category = getCategories(articleOnString);

            Article article = new Article(id, publicationDate, title, summary, link, comment, author, category);
            listArticles.add(article);
        }
        return listArticles;
    }


    /**
     * Get the Id of the arxiv article, which corresponds to the arxiv link of the article.
     *
     * @param string : the String in atom format corresponding to one article only.
     * @return the arxiv link used as Id of the article.
     */
    private static String getId(String string){
        return string.substring(string.indexOf("<id>") + 4, string.indexOf("</id>"));
    }


    /**
     * Generate a LocalDate object from the String representation of the publication date.
     *
     * @param string : the String in atom format corresponding to one article only.
     * @return the LocalDate object matching the publication date of the article.
     */
    private static LocalDate getPublicationDate(String string){
        String dateString = string.substring(string.indexOf("<published>") + 11, string.indexOf("</published>"));
        dateString = dateString.substring(0, dateString.indexOf("T"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(dateString, format);
    }


    private static String getTitle(String string){
        String title = string.substring(string.indexOf("<title>") + 7, string.indexOf("</title>"));
        title = title.replace("\n","").replace("\r", "").trim();
        return title;
    }


    private static String getSummary(String string){
        String summary = string.substring(string.indexOf("<summary>") + 11, string.indexOf("</summary>"));
        summary = summary.replace("\n","").replace("\r", "").trim();
        return summary;
    }


    private static String getName(String string){
        return string.substring(string.indexOf("<name>") + 6, string.indexOf("</name>"));
    }


    /**
     * Generate a list of Author from the authors in the String in atom format passed in parameter.
     * If the name takes more than 3 words, the remaining words are put in the last attribute (lastName),
     * separated by a space.
     *
     * @param string : the String in atom format corresponding to one article only.
     * @return the list of authors corresponding.
     */
    private static List<Author> getAuthors(String string){

        String[] tabAuthors = string.split("<author>");
        List<Author> authorsList = new ArrayList<>();

        for(int i = 1 ; i < tabAuthors.length  ; i++){
            String[] authorString = getName(tabAuthors[i]).split(" ");
            if(authorString.length >2){
                String firstName = authorString[0];
                String middleName = authorString[1];
                StringBuilder lastName = new StringBuilder();
                for(int j = 2 ; j < authorString.length ; j++){
                    if(j == authorString.length -1){
                        lastName.append(authorString[j]);
                    }
                    else {
                        lastName.append(authorString[j]).append(" ");
                    }
                }

                Author author = new Author(firstName,middleName, lastName.toString());
                authorsList.add(author);
            }
            else {
                String firstName = authorString[0];
                String lastName = authorString[1];
                Author author = new Author(firstName,lastName);
                authorsList.add(author);
            }
        }
        return authorsList;
    }


    private static String getDownloadLink(String string){
        return string.substring(string.lastIndexOf("href=\"") + 6, string.lastIndexOf("\" rel"));
    }


    private static List<String> getCategories(String string){
        List<String> listCategories = new ArrayList<>();
        String[] tabCategories = string.split("<category term=\"");

        for(int i = 1; i < tabCategories.length ; i++){
           String category =  tabCategories[i].substring(0,tabCategories[i].indexOf("\" scheme"));
           listCategories.add(category);
        }
        return listCategories;
    }


    private static String getComment(String string){
        int index = string.indexOf("</arxiv:comment>");

        if(index != -1){
            String s =  string.substring(string.lastIndexOf(
                    "<arxiv:comment xmlns:arxiv=\"http://arxiv.org/schemas/atom\">") + 59, index);
            s = s.replace("\n","").replace(" \r", "").trim();
            return s.replace("  ", " ");
        }
        return null;
    }

}
