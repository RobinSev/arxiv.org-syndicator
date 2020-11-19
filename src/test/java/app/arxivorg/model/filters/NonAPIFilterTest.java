package app.arxivorg.model.filters;

import app.arxivorg.model.Article;
import app.arxivorg.model.AtomParser;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NonAPIFilterTest {

    List<Article> articles = AtomParser.parse(
            Paths.get("src/test/java/app/arxivorg/model/resources/TestFile.atom.xml"));

    @Test
    public void testFilter(){
        NonAPIFilter filter0Article = new FilterByDate(LocalDate.of(2020, 3, 11));
        NonAPIFilter filter1Article = new FilterByDate(LocalDate.of(2020, 3, 10));
        NonAPIFilter filter2Article = new FilterByDate(LocalDate.of(2019, 4, 1));
        NonAPIFilter filterAllArticle = new FilterByDate(LocalDate.of(2019, 1, 30));

        List<Article> result = filter0Article.filter(articles);
        assertTrue(result.isEmpty());

        result = filter1Article.filter(articles);
        assertEquals(1, result.size());
        assertEquals("arxiv link article 1", result.get(0).getArxivLink());

        result = filter2Article.filter(articles);
        assertEquals(2, result.size());
        assertEquals("arxiv link article 1", result.get(0).getArxivLink());
        assertEquals("arxiv link article 2", result.get(1).getArxivLink());

        result = filterAllArticle.filter(articles);
        assertEquals(3, result.size());
        assertEquals("arxiv link article 1", result.get(0).getArxivLink());
        assertEquals("arxiv link article 2", result.get(1).getArxivLink());
        assertEquals("arxiv link article 3", result.get(2).getArxivLink());
    }

    @Test
    public void testEquals() {
        LocalDate date1Equal = LocalDate.of(2020, 11, 3);
        LocalDate date2Equal = LocalDate.of(2020, 11, 3);
        LocalDate date1Different = LocalDate.of(2020, 11, 2);
        LocalDate date2Different = LocalDate.of(2020, 10, 3);

        NonAPIFilter filter1Equal = new FilterByDate(date1Equal);
        NonAPIFilter filter2Equal = new FilterByDate(date2Equal);
        NonAPIFilter filter1Different = new FilterByDate(date1Different);
        NonAPIFilter filter2Different = new FilterByDate(date2Different);

        assertEquals(filter1Equal, filter2Equal);
        assertNotEquals(filter1Equal, filter1Different);
        assertNotEquals(filter2Equal, filter2Different);

    }

}
