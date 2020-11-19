package app.arxivorg.commandline;

import app.arxivorg.controller.ArxivCLIController;
import app.arxivorg.controller.ListHandler;
import app.arxivorg.model.Article;
import app.arxivorg.model.AtomParser;
import app.arxivorg.model.Author;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ArxivCLIControllerTest {

    private final ArxivCLIController controller1 = new ArxivCLIController(5, true);
    private final ArxivCLIController controller2 = new ArxivCLIController(5, false);
    private List<Author> authorsArticle1 = List.of(new Author("Thierry", "Lafronde"), new Author("Camille", "Drouin"));
    private List<Author> authorsArticle2 = List.of(new Author("Iva", "Deschênes"), new Author("Sennet", "Daigle"));
    private List<Author> authorsArticle3 = List.of(new Author("Shane", "J.", "Rubenstein"), new Author("Lisa", "V", "Cook"));
    private List<String> categories1 = List.of("Quantum physic", "Astrophysic");
    private List<String> categories2 = List.of("Computation and language", "Game theory", "Linear algebra");
    private List<String> categories3 = List.of("Linear Algebra", "Statistics");
    private final List<Article> articles1 = AtomParser.parse(Paths.get("file.atom.xml"));
    private List<Article> articles2 = List.of(
            new Article("arXiv:2004.02884", LocalDate.of(2020, 4, 8), "Article 1", "summary 1", "https://download1.com", "comments 1", authorsArticle1, categories1),
            new Article("arXiv:2003.04568", LocalDate.of(2019, 10, 24), "Article 2", "summary 2", "https://download2.com", "comments 2", authorsArticle2, categories2),
            new Article("arXiv:2004.05(68", LocalDate.of(2020, 1, 27), "Article 3", "summary 3", "https://download3.com", "comments 3", authorsArticle3, categories3)
    );
    private ListHandler listHandler;

    @BeforeEach
    public void setUp() {
        listHandler = new ListHandler();
        listHandler.setArticles(articles1);
        controller1.setSelected(List.of(0, 1, 2, 3, 4, 5));
    }


    @Test
    public void testIsInterpreterMode() {
        assertTrue(controller1.isInterpreterMode());
        assertFalse(controller2.isInterpreterMode());
    }


    @Test
    public void testDownloadSelected() {
        assertEquals(-3, controller1.downloadSelected("/"));
    }

    @Test
    public void testSetSelected() {
        assertEquals(1, controller1.setSelected(List.of(0)));
        assertNotEquals(0, controller1.setSelected(List.of(0, 1, 2)));
    }

    /**
     * The download all method only returns -3 because it's impossible to initialize the list of articles without making a request.
     */
    @Test
    public void testDownloadAll() {
        assertEquals(-3, controller1.downloadAll("/", Map.of("category", "notACategory", "period", "2020-02-10")));
        assertEquals(-1, controller1.downloadAll("notAPath", Map.of("category", "cs.CL", "period", "2020-02-10")));
        assertEquals(-3, controller1.downloadAll("/", Map.of("category", "cs.CL", "period", "notAPeriod")));
    }

    @Test
    public void testRequestArticles() {
        assertNull(controller1.requestArticles(Map.of("category", "cs.CL", "period", "notAPeriod")));
        assertNull(controller1.requestArticles(Map.of("category", "", "period", "2020-02-10")));

    }


}
