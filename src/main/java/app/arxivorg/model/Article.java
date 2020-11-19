package app.arxivorg.model;

import java.time.LocalDate;
import java.util.List;

public class Article {

    private final String arxivLink;
    private final LocalDate publicationDate;
    private final String title;
    private final String summary;
    private final String downloadLink;
    private final String comment;
    private final List<Author> authors;
    private final List<String> categories;

    public Article(String arxivLink, LocalDate publicationDate, String title, String summary, String downloadLink,
                   String comment, List<Author> authors, List<String> categories) {
        this.arxivLink = arxivLink;
        this.publicationDate = publicationDate;
        this.title = title;
        this.summary = summary;
        this.downloadLink = downloadLink;
        this.comment = comment != null ? comment : "";
        this.authors = authors;
        this.categories = categories;
    }

    public String getArxivLink() {
        return arxivLink;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getComment() {
        return comment;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public String getPrimaryCategory() { return categories.get(0); }

    public List<String> getCategories() {
        return categories;
    }


    /**
     * Test if the Article is equals to another java object.
     * Override the Object equals.
     *
     * @param o : the object we want to test the equality with.
     * @return {@code true} if they are both articles with all their attributes equal. {@code false} otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return arxivLink.equals(article.arxivLink) &&
                publicationDate.equals(article.publicationDate) &&
                title.equals(article.title) &&
                summary.equals(article.summary) &&
                downloadLink.equals(article.downloadLink) &&
                comment.equals(article.comment) &&
                authors.equals(article.authors) &&
                categories.equals(article.categories);
    }
}
