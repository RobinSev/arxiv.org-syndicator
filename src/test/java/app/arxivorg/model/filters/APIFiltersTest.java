package app.arxivorg.model.filters;
import app.arxivorg.model.filters.apifilters.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class APIFiltersTest {

    String titleAndAbstract = "développement langage, compilation";
    String categories = "cs.CL, q-bio.GN, physics.optics";
    String authorsNames = "Bernard Glandu, Charline LaPraline";
    String query = "query?";

    APIFilter filterByCat = new FilterByCategories(categories);
    APIFilter filterByTitle = new FilterByTitle(titleAndAbstract);
    APIFilter filterByAuthors = new FilterByAuthors(authorsNames);
    APIFilter filterByAbstract = new FilterByAbstract(titleAndAbstract);

    @Test
    public void testConstructors() {

        assertEquals("cs.CL", filterByCat.getParameters()[0]);
        assertEquals("q-bio.GN", filterByCat.getParameters()[1]);
        assertEquals("physics.optics", filterByCat.getParameters()[2]);

        assertEquals("développement langage", filterByTitle.getParameters()[0]);
        assertEquals("compilation", filterByTitle.getParameters()[1]);

        assertEquals("développement langage", filterByAbstract.getParameters()[0]);
        assertEquals("compilation", filterByAbstract.getParameters()[1]);

        assertEquals("Bernard Glandu", filterByAuthors.getParameters()[0]);
        assertEquals("Charline LaPraline", filterByAuthors.getParameters()[1]);
    }

    @Test
    public void testAddParameters(){

        assertEquals("query?au:\"Bernard Glandu\" AND au:\"Charline LaPraline\"",
                filterByAuthors.addParametersToQuery(query));
        assertEquals("query?cat:cs.CL AND cat:q-bio.GN AND cat:physics.optics",
                filterByCat.addParametersToQuery(query));
        assertEquals("query?ti:\"développement langage\" AND ti:\"compilation\"",
                filterByTitle.addParametersToQuery(query));
        assertEquals("query?abs:\"développement langage\" AND abs:\"compilation\"",
                filterByAbstract.addParametersToQuery(query));
    }

    @Test
    public void testAddMultipleParameters() {
        String finalQuery = filterByCat.addParametersToQuery(query) + " AND ";
        finalQuery = filterByAuthors.addParametersToQuery(finalQuery) + " AND ";
        finalQuery = filterByTitle.addParametersToQuery(finalQuery);

        String queryExpected = "query?cat:cs.CL AND cat:q-bio.GN AND cat:physics.optics AND " +
                "au:\"Bernard Glandu\" AND au:\"Charline LaPraline\" AND " +
                "ti:\"développement langage\" AND ti:\"compilation\"";

        assertEquals(queryExpected, finalQuery);
    }

    @Test
    public void testEquals() {
        APIFilter filterByCategories1 = new FilterByCategories("cat1, cat2");
        APIFilter filterByCategories2 = new FilterByCategories("cat2, cat1");
        APIFilter filterByCategories3 = new FilterByCategories("cat1, cat2");
        APIFilter filterByAuthors = new FilterByAuthors("cat1, cat2");

        assertEquals(filterByCategories1, filterByCategories3);
        assertEquals(filterByCategories1, filterByCategories2);
        assertNotEquals(filterByAuthors, filterByCategories1);
    }
}
