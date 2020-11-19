package app.arxivorg.controller.JUnitTests;

import app.arxivorg.controller.ListHandler;
import app.arxivorg.model.Article;
import app.arxivorg.model.AtomParser;
import app.arxivorg.model.Author;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.TextFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.testfx.assertions.api.Assertions.assertThat;


public class ListHandlerTest {

    private List<Article> articles1 = AtomParser.parse(Paths.get("file.atom.xml"));
    private ListHandler listHandler;
    private List<Author> authorsArticle1 = List.of(new Author("Thierry", "Lafronde"), new Author("Camille", "Drouin"));
    private List<Author> authorsArticle2 = List.of(new Author("Iva", "Deschênes"), new Author("Sennet", "Daigle"));
    private List<Author> authorsArticle3 = List.of(new Author("Shane", "J.", "Rubenstein"), new Author("Lisa", "V", "Cook"));
    private List<String> categories1 = List.of("Quantum physic", "Astrophysic");
    private List<String> categories2 = List.of("Computation and language", "Game theory", "Linear algebra");
    private List<String> categories3 = List.of("Linear Algebra", "Statistics");
    private List<Article> articles2 = List.of(
            new Article("arXiv:2004.02884", LocalDate.of(2020, 4, 8), "Article 1", "summary 1", "https://download1.com", "comments 1",authorsArticle1, categories1),
            new Article("arXiv:2003.04568", LocalDate.of(2019, 10, 24), "Article 2", "summary 2", "https://download2.com", "comments 2", authorsArticle2, categories2),
            new Article("arXiv:2004.05(68", LocalDate.of(2020, 1, 27), "Article 3", "summary 3", "https://download3.com", "comments 3", authorsArticle3, categories3)
    );


    @BeforeEach
    private void createArticlesList() {
        this.listHandler = new ListHandler();
    }

    @Test
    public void testGetArticles() {
        assertEquals(new ArrayList<>(), listHandler.getArticles());
        listHandler.getArticles().addAll(List.of(articles1.get(5), articles1.get(7)));
        assertEquals(List.of(articles1.get(5), articles1.get(7)), listHandler.getArticles());
    }

    @Test
    public void testGetSelected() {
        assertEquals(new ArrayList<>(), listHandler.getSelected());
        listHandler.getSelected().add(articles1.get(3));
        assertEquals(List.of(articles1.get(3)), listHandler.getSelected());
        listHandler.getSelected().add(articles1.get(2));
        assertEquals(List.of(articles1.get(3), articles1.get(2)), listHandler.getSelected());
        assertNotEquals(List.of(articles1.get(2), articles1.get(3)), listHandler.getSelected());
    }

    @Test
    public void testEmptyConstructor() {
        ListHandler listHandler = new ListHandler();
        assertEquals(new ArrayList<>(), listHandler.getArticles());
        assertEquals(new ArrayList<>(), listHandler.getSelected());
    }


    @Test
    public void testSetArticles() {
        assertEquals(new ArrayList<>(), listHandler.getArticles());
        listHandler.setArticles(articles2);
        assertEquals(articles2, listHandler.getArticles());
        listHandler.setArticles(articles1);
        assertEquals(articles1, listHandler.getArticles());
        assertNotEquals(List.of(articles2, articles1), listHandler.getArticles());
    }

    @Test
    public void testSetSelected() {
        assertEquals(new ArrayList<>(), listHandler.getSelected());
        listHandler.setSelected(articles2);
        assertEquals(articles2, listHandler.getSelected());
        listHandler.setSelected(articles1);
        assertEquals(articles1, listHandler.getSelected());
        assertNotEquals(List.of(articles2, articles1), listHandler.getSelected());
        assertEquals(new ArrayList<>(), listHandler.getArticles());
    }

    @Test
    public void testSetSelectedIndexes() {
        assertEquals(1, listHandler.setSelectedIndexes(List.of(2)));
        assertEquals(new ArrayList<>(), listHandler.getSelected());
        listHandler.setArticles(articles1);
        assertEquals(0, listHandler.setSelectedIndexes(List.of(2)));
        assertEquals(List.of(articles1.get(2)), listHandler.getSelected());
        assertEquals(1, listHandler.setSelectedIndexes(List.of(-1)));
        assertEquals(List.of(articles1.get(2)), listHandler.getSelected());
        assertEquals(1, listHandler.setSelectedIndexes(List.of(10)));
        assertEquals(List.of(articles1.get(2)), listHandler.getSelected());
    }

    @Test
    public void testAdd() {
        assertEquals(new ArrayList<>(), listHandler.getArticles());
        Article article1 = articles1.get(0);
        Article article2 = articles2.get(0);
        listHandler.add(article1);
        assertEquals(List.of(article1), listHandler.getArticles());
        listHandler.add(article2);
        assertEquals(List.of(article1, article2), listHandler.getArticles());
        assertEquals(new ArrayList<>(), listHandler.getSelected());
    }

    @Test
    public void addAllInArticles() {
        assertEquals(new ArrayList<>(), listHandler.getArticles());
        listHandler.addAllInArticlesList(articles1);
        assertEquals(articles1, listHandler.getArticles());
        List<Article> testArticlesList = new ArrayList<>(articles1);
        testArticlesList.addAll(articles2);
        listHandler.addAllInArticlesList(articles2);
        assertEquals(testArticlesList, listHandler.getArticles());
        assertEquals(new ArrayList<>(), listHandler.getSelected());
    }




    @Test
    public void testGetMetadata() {
        listHandler.setArticles(articles1);
        HBox metadatas = listHandler.getMetadata(2);
        assertEquals(10, metadatas.getSpacing());
        ObservableList<Node> children = metadatas.getChildren();
        assertEquals(1, children.size());
        assertEquals(TextFlow.class, children.get(0).getClass());
        TextFlow container = (TextFlow) children.get(0);
        assertThat(container).hasExactlyNumChildren(3);
    }


/*
    The following test throws an 'java.lang.IllegalStateException: Toolkit not initialized' exception.
    After some checks, it appears that the test does not support the JavaFX Object constructors
    (for checkBoxes and Label).

    @Test
    public void testGetDisplayableItems() {
        ObservableList<HBox> items = listHandler.getDisplayableItems();
        for (int i = 0; i < items.size(); i++) {
            assertEquals(HBox.class, items.get(i).getClass());
            ObservableList<Node> children = items.get(i).getChildren();
            assertEquals(2, children.size());
            assertThat(children.get(0).getClass()).isEqualTo(CheckBox.class);
            assertThat(children.get(1).getClass()).isEqualTo(TextFlow.class);
            assertThat((TextFlow) children.get(1)).hasExactlyNumChildren(3);
        }
    }
 */
}
