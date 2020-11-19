package app.arxivorg.model;

import app.arxivorg.model.filters.apifilters.APIFilter;
import app.arxivorg.model.filters.apifilters.FilterByAuthors;
import app.arxivorg.model.filters.apifilters.FilterByCategories;
import app.arxivorg.model.filters.apifilters.FilterByTitle;
import app.arxivorg.model.filters.nonapifilters.FilterByDate;
import app.arxivorg.model.filters.nonapifilters.NonAPIFilter;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RequestHandlerTest {

    RequestHandler requestHandler = new RequestHandler();
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date = LocalDate.parse("1980-01-03", format);
    LocalDate date2 = LocalDate.parse("2021-03-03", format);

    @Test
    public void filterDateTest() throws Exception {
        List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
        nonAPIFilters.add(new FilterByDate(date));

        List<Article> list = requestHandler.request(new ArrayList<>(), nonAPIFilters,50);
        assertFalse(list.isEmpty());
        for(Article element : list){
            assertTrue(element.getPublicationDate().isAfter(date));
        }
        assertEquals(50, list.size());

        nonAPIFilters.clear();
        nonAPIFilters.add(new FilterByDate(date2));
        List<Article> list2 = requestHandler.request(new ArrayList<>(), nonAPIFilters,50);
        assertEquals(0, list2.size());

        LocalDate threeDaysBefore = LocalDate.now().minusDays(3);
        nonAPIFilters.clear();
        nonAPIFilters.add(new FilterByDate(threeDaysBefore));

        long before = System.currentTimeMillis();
        requestHandler.request(new ArrayList<>(), nonAPIFilters, 50);
        long after = System.currentTimeMillis();
        assertTrue(after - before < 2 * 1000);
    }

    @Test
    public void filterCategoryTest() throws Exception {
        APIFilter filter = new FilterByCategories("cs.CL");
        ArrayList<APIFilter> apiFilters = new ArrayList<>();
        apiFilters.add(filter);
        List<Article> list3 = requestHandler.request(apiFilters, new ArrayList<>(),50);
        assertFalse(list3.isEmpty());
        for(Article element : list3){
            assertTrue(element.getCategories().contains("cs.CL"));
        }
    }

    @Test
    public void filterAuthorsTest() throws Exception {
        APIFilter filter2 = new FilterByAuthors("Robin Harper");
        ArrayList<APIFilter> apiFilters = new ArrayList<>();
        apiFilters.add(filter2);
        List<Article> list4 = requestHandler.request(apiFilters, new ArrayList<>(),5);
        assertFalse(list4.isEmpty());
        for(Article element : list4){
            assertTrue(element.getAuthors().toString().contains("Robin Harper"));
        }
    }


    @Test
    public void filterTitleTest() throws Exception {
        APIFilter filter3 = new FilterByTitle("Modelling Users, Intentions, and Structure in Spoken Dialog");
        ArrayList<APIFilter> apiFilters = new ArrayList<>();
        apiFilters.add(filter3);
        List<Article> list5 = requestHandler.request(apiFilters, new ArrayList<>(),1);
        assertFalse(list5.isEmpty());
        for(Article element : list5){
            assertEquals("Modelling Users, Intentions, and Structure in Spoken Dialog",element.getTitle());
        }
    }

    @Test
    public void eachCharacteristicTest() throws Exception {
        APIFilter filter4 = new FilterByTitle("Conditions on Consistency of Probabilistic Tree Adjoining Grammars");
        ArrayList<APIFilter> apiFilters = new ArrayList<>();
        apiFilters.add(filter4);
        List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
        nonAPIFilters.add(new FilterByDate(date));
        List<Article> list6 = requestHandler.request(apiFilters, nonAPIFilters,1);
        assertFalse(list6.isEmpty());
        for(Article element : list6){
            assertEquals("Conditions on Consistency of Probabilistic Tree Adjoining Grammars",element.getTitle());
            assertEquals("Anoop Sarkar",element.getAuthors().get(0).toString());
            assertEquals("7 pages, 4 Postscript figures, uses colacl.sty, graphicx.sty, psfrag.sty", element.getComment());
            assertEquals("cs.CL", element.getPrimaryCategory());
            assertEquals("cs.CL",element.getCategories().get(0));
            assertEquals("I.2.7; D.3.1", element.getCategories().get(1));
            assertEquals("http://arxiv.org/abs/cs/9809027v1", element.getArxivLink());
            assertEquals(LocalDate.parse("1998-09-18"), element.getPublicationDate());
            assertEquals("http://arxiv.org/pdf/cs/9809027v1", element.getDownloadLink());
            assertEquals(1,element.getAuthors().size());
            assertEquals(2, element.getCategories().size());
        }
    }

    @Test
    public void multipleFiltersTest() throws Exception {
        APIFilter filterByTitle = new FilterByTitle("Conditions on Consistency of Probabilistic Tree Adjoining Grammars");
        APIFilter filterByCategories = new FilterByCategories("cs.CL");
        ArrayList<APIFilter> listFilter = new ArrayList<>();
        listFilter.add(filterByTitle);
        listFilter.add(filterByCategories);

        List<Article> list7 = requestHandler.request(listFilter, new ArrayList<>(),2);
        assertFalse(list7.isEmpty());
        for(Article element : list7){
            assertTrue(element.getCategories().contains("cs.CL"));
            assertEquals("Conditions on Consistency of Probabilistic Tree Adjoining Grammars" , element.getTitle());
        }

        listFilter.clear();
        listFilter.add(new FilterByCategories("cs.CL"));
        listFilter.add(new FilterByAuthors("Robin Harper"));

        List<Article> noResultExpected = requestHandler.request(listFilter, new ArrayList<>(), 50);
        assertTrue(noResultExpected.isEmpty());
    }

    @Test
    public void testRequestMoreArticles() throws Exception {
        List<NonAPIFilter> nonAPIFilters = new ArrayList<>();
        nonAPIFilters.add(new FilterByDate(date));

        ArrayList<APIFilter> apiFilters = new ArrayList<>();
        apiFilters.add(new FilterByCategories("cs.CL"));

        List<Article> references = requestHandler.request(apiFilters, nonAPIFilters, 10);
        assertEquals(10, references.size());
        for (Article article : references) {
            assertTrue(article.getPublicationDate().isAfter(date));
            assertTrue(article.getCategories().contains("cs.CL"));
        }

        List<Article> addedArticles = requestHandler.requestMoreArticles(references);
        assertEquals(10, addedArticles.size());
        for (Article article : addedArticles) {
            assertTrue(article.getPublicationDate().isAfter(date));
            assertTrue(article.getCategories().contains("cs.CL"));
            assertFalse(references.contains(article));
        }

        List<Article> secondWave = requestHandler.requestMoreArticles(addedArticles);
        assertEquals(10, secondWave.size());
        for (Article article : secondWave) {
            assertTrue(article.getPublicationDate().isAfter(date));
            assertTrue(article.getCategories().contains("cs.CL"));
            assertFalse(references.contains(article));
            assertFalse(addedArticles.contains(article));
        }

    }

}
