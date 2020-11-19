package app.arxivorg.model;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AtomParserTest {

    List<Article> Articles = AtomParser.parse(
            Paths.get("src/test/java/app/arxivorg/model/resources/TestFile.atom.xml"));

    @Test
    public void testPrimaryCategory() {
        assertEquals("primary category article 1", Articles.get(0).getPrimaryCategory());
        assertEquals("primary category article 2", Articles.get(1).getPrimaryCategory());
        assertEquals("primary category article 3", Articles.get(2).getPrimaryCategory());
    }

    @Test
    public void testAuthors(){
        assertEquals("author3 article 1", Articles.get(0).getAuthors().get(2).toString());
        assertEquals("author1 article 1", Articles.get(0).getAuthors().get(0).toString());
        assertEquals("author2 article 2 with more names", Articles.get(1).getAuthors().get(1).toString());
        assertEquals("author4 article 3", Articles.get(2).getAuthors().get(3).toString());
        assertEquals("author12 article 2", Articles.get(1).getAuthors().get(11).toString());
    }
    @Test
    public void testId(){
        assertEquals("arxiv link article 1", Articles.get(0).getArxivLink());
        assertEquals("arxiv link article 2", Articles.get(1).getArxivLink());
        assertEquals("arxiv link article 3", Articles.get(2).getArxivLink());
    }
    @Test
    public void testSummary(){
        assertEquals("summary article 1.", Articles.get(0).getSummary());
        assertEquals("summary article 3.", Articles.get(2).getSummary());
    }
    @Test
    public void testCategories(){
        assertEquals("cat2 article 1", Articles.get(0).getCategories().get(1));
        assertEquals("cat3 article 1", Articles.get(0).getCategories().get(2));
        assertEquals("primary category article 3", Articles.get(2).getCategories().get(0));
    }
    @Test
    public void testPublicationDate(){
       assertEquals(LocalDate.parse("2020-03-10"), Articles.get(0).getPublicationDate());
       assertEquals(LocalDate.parse("2020-03-08"), Articles.get(1).getPublicationDate());
       assertEquals(LocalDate.parse("2019-03-22"), Articles.get(2).getPublicationDate());
    }
    @Test
    public void testDownloadLink(){
        assertEquals("download link article 1", Articles.get(0).getDownloadLink());
        assertEquals("download link article 2", Articles.get(1).getDownloadLink());
        assertEquals("download link article 3", Articles.get(2).getDownloadLink());
    }
    @Test
    public void testComment(){
        assertEquals("", Articles.get(0).getComment());
        assertEquals("comment article 2", Articles.get(1).getComment());
        assertEquals("comment article 3", Articles.get(2).getComment());
    }
    @Test
    public void testTitle(){
        assertEquals("title article 1", Articles.get(0).getTitle());
        assertEquals("title article 2", Articles.get(1).getTitle());
        assertEquals("title article 3", Articles.get(2).getTitle());
    }
}
