package app.arxivorg.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArticleTest {
    Author author1 = new Author("Thierry", "Lafronde");
    Author author2 = new Author("Jean-Marc", "Levy");

    List<Author> authors1 = new ArrayList<>();
    List<Author> authors2 = new ArrayList<>();

    List<String> categories = new ArrayList<>();

    LocalDate dateArticle1;
    LocalDate dateArticle2;

    Article article1;
    Article article2;

    @BeforeEach
    private void constructArticles() {
        authors1.add(author1);
        authors2.add(author1);
        authors2.add(author2);

        categories.add("IA");
        categories.add("Computer Science");

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateArticle1 = LocalDate.parse("2012-12-01", format);
        dateArticle2 = LocalDate.parse("1993-12-01", format);

        article1 = new Article("link to arxiv1", dateArticle1, "holidays", "holidays to the beach",
                "link to download1", "great article", authors1, categories);
        article2 = new Article("link to arxiv2",dateArticle2, "murder", "murder on the beach",
                "link to download2", null, authors2, categories);
    }

    @Test
    public void testGetArxivlink() {
        assertEquals("link to arxiv1", article1.getArxivLink());
        assertEquals("link to arxiv2", article2.getArxivLink());
    }


    @Test
    public void testGetPublicationDate() {
        assertEquals(dateArticle1, article1.getPublicationDate());
        assertEquals(dateArticle2, article2.getPublicationDate());
    }


    @Test
    public void testGetTitle() {
        assertEquals("holidays", article1.getTitle());
        assertEquals("murder", article2.getTitle());
    }


    @Test
    public void testGetSummary() {
        assertEquals("holidays to the beach", article1.getSummary());
        assertEquals("murder on the beach", article2.getSummary());
    }


    @Test
    public void testGetDownloadLink() {
        assertEquals("link to download1", article1.getDownloadLink());
        assertEquals("link to download2", article2.getDownloadLink());
    }


    @Test
    public void testGetComment() {
        assertEquals("great article", article1.getComment());
        assertEquals("", article2.getComment());
    }


    @Test
    public void testGetAuthors() {
        assertIterableEquals(authors1, article1.getAuthors());
        assertIterableEquals(authors2, article2.getAuthors());
    }


    @Test
    public void testGetCategories() {
        assertIterableEquals(categories, article1.getCategories());
        assertIterableEquals(categories, article2.getCategories());
    }


    @Test
    public void testGetPrimaryCategory() {
        assertEquals("IA", article1.getPrimaryCategory());
        assertEquals("IA", article2.getPrimaryCategory());
    }


    @Test
    public void testEquals() {
        authors1.clear();
        authors1.add(author1);
        authors1.add((author2));

        authors2.clear();
        authors2.add(author2);
        authors2.add(author1);

        article1 = new Article("link to arxiv1",dateArticle1, "holidays", "holidays to the beach",
                "link to download1", "great article", authors1, categories);
        article2 = new Article("link to arxiv1",dateArticle1, "holidays", "holidays to the beach",
                "link to download1", "great article", authors2, categories);
        Article article3 = new Article("link to arxiv1",dateArticle1, "holidays", "holidays to the beach",
                "link to download1", "great article", authors1, categories);
        Article article4 = new Article("link to arxiv1",dateArticle1, "holidays", "holidays to the beach",
                "link to download1", null, authors1, categories);

        assert(!article1.equals(article2));
        assert(article1.equals(article3));
        assert(!article1.equals(article4));
    }

}
