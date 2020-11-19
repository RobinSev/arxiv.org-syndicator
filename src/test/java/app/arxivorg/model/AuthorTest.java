package app.arxivorg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthorTest {

    Author author1 = new Author("Thierry", "Lafronde");
    Author author2 = new Author("Charlène", "M.", "Miappa");
    Author author3 = new Author("Thierry", "", "Lafronde");
    Author author4 = new Author("Merry", "Lafronde");
    Author author5 = new Author("Thierry", "Lafronde");
    Author author6 = new Author("Thierry", "LaFronde");

    @Test
    public void testToString() {
        assertEquals("Thierry Lafronde", author1.toString());
        assertEquals("Thierry Lafronde", author3.toString());
        assertEquals("Charlène M. Miappa", author2.toString());
    }

    @Test
    public void testEquals() {
        assertEquals(author1, author3);
        assertEquals(author1, author5);
        assertNotEquals(author1, author4);
        assertNotEquals(author1, author6);
        assertNotEquals(author1, author2);
    }
}
