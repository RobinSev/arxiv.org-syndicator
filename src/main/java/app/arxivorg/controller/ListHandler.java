package app.arxivorg.controller;

/*
	JAVAFX IMPORTS
 */
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/*
	PROJECTS AND JAVA EXTERNAL LIBRARIES IMPORTS
 */
import app.arxivorg.model.Author;
import app.arxivorg.model.Article;
import java.util.*;

public class ListHandler {

	private List<Article> articles;
	private List<Article> selected;


	/**
	 * Constructs an instance with an empty
	 * list of articles and an empty list
	 * of selected ones.
	 */
	public ListHandler() {
		this.articles = new ArrayList<>();
		this.selected = new ArrayList<>();
	}


	public void addAllInArticlesList(List<Article> articles) {
		this.articles.addAll(articles);
	}

	public boolean add(Article article) {
		return this.articles.add(article);
	}

	/**
	 * Returns, as a list of HBox, all the displayable items for the listView.
	 * Each item corresponds to a displayable article.
	 *
	 * @return the list of the displayable items for the listView.
	 */
	public ObservableList<HBox> getDisplayableItems() {
		ObservableList<HBox> items = FXCollections.observableArrayList();
		int i = 0;
		for (Article article: articles) {
			items.add(getItem(i, article));
			i++;
		}
		return items;
	}


	/**
	 * Sets the {@code selected} list of articles to the specified list.
	 *
	 * @param articlesSelected - The list of the selected articles.
	 */
	public void setSelected (List<Article> articlesSelected) {
		this.selected = articlesSelected;
	}

	/**
	 * Sets the {@code selected} list of articles to a new list of articles
	 * containing the articles which the indexes in {@code articles} are
	 * specified in {@code selectedIndexes}
	 *
	 * @param selectedIndexes - The list of the indexes of the selected articles.
	 */
	public int setSelectedIndexes(List<Integer> selectedIndexes) {
		for (Integer index : selectedIndexes) {
			if (index >= articles.size() || index < 0) return 1;
			selected.add(articles.get(index));
		}
		return 0;
	}


	/**
	 * Returns as a HBox, the specified {@code article} as a displayable item.
	 * A displayable item is a HBox containing a checkbox and a textFlow.
	 * The TextFlow is used as a container for the title, the arxivLink and
	 * the list of authors of the articles.
	 *
	 * @param indexInArticles - The id associated to the article (it might be unique)
	 * @param article - The article to get displayable cell from.
	 *
	 * @return a HBox corresponding to a displayable list item for the specified article.
	 */
	private HBox getItem(int indexInArticles, Article article) {
		Label itemIndex = new Label(String.valueOf(indexInArticles));
		itemIndex.setVisible(false);
		CheckBox checkBox = new CheckBox();
		checkBox.setOnAction(this::handleSelectedCheckBox);
		if (this.selected.contains(article))
			checkBox.setSelected(true);
		Text title = new Text(article.getTitle() + "\n");
		Text arxivLink = new Text(article.getArxivLink() + "\t");
		Text authors = new Text(parseAuthorsList(article.getAuthors()));
		return new HBox(10, checkBox, new TextFlow(title, arxivLink, authors), itemIndex);
	}

	/**
	 * A method call by each checkbox in the listView to add the article
	 * it is linked with to the selected list if selected, or remove it from
	 * that list if not.
	 *
	 * @param event - The event creating by selecting the checkBox.
	 */
	private void handleSelectedCheckBox(Event event) {
		if (!(event.getSource() instanceof CheckBox)) return;
		CheckBox checkBox = (CheckBox) event.getSource();
		if (!(checkBox.getParent() instanceof HBox)) return;
		HBox container = (HBox) checkBox.getParent();
		if (!(container.getChildren().get(2) instanceof Label)) return;
		int idInArticles = Integer.parseInt(((Label) container.getChildren().get(2)).getText());
		if (idInArticles >= this.articles.size() || idInArticles < 0) return;
		if (checkBox.isSelected()) {
			this.selected.add(this.articles.get(idInArticles));
		} else {
			this.selected.remove(this.articles.get(idInArticles));
		}
	}


	/**
	 * Returns the String object corresponding to the specified list of {@code authors}
	 * In the returned String object, the firstname and lastname of an author are
	 * separated from the others by comma.
	 *
	 * E.g : when the input is {@code List.of(new Author("Yann", "LeCun"), new Author("Geoffrey", "Hinton")}
	 *       the returned string is : "Yann LeCun, Geoffrey Hinton"
	 *
	 * @param authors the authors list to parse.
	 *
	 * @return the String object corresponding to the specified list of authors.
	 */
	private String parseAuthorsList(List<Author> authors) {
		if (authors.isEmpty()) return "";
		String parsedAuthors = authors.get(0).toString();
		for (int i = 1; i < authors.size(); i++) {
			parsedAuthors += ", " + authors.get(i).toString();
		}
		return parsedAuthors;
	}

	/**
	 * Sets the property articles to the specified list {@code articles}
	 *
	 * @param articles - The new list of articles.
	 */
	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	/**
	 *
	 * @return the list of articles.
	 */
	public List<Article> getArticles() {
		return this.articles;
	}

	/**
	 *
	 * @return the list of the articles selected buy the user.
	 */
	public List<Article> getSelected() {
		return this.selected;
	}

	/**
	 * Returns, as a HBox, the metadatas of the article.
	 * More precisely, the HBox contains a TextFlow, this latter
	 * containing the title, the authors' names, the summary, and
	 * the ArxivLink of the article corresponding to the specified
	 * index.
	 *
	 * @param articleIndex the index of the article in the {@code articles} list.
	 *
	 * @return the HBox containing the metadatas of the article.
	 */
	public HBox getMetadata(int articleIndex) {
		Article article = articles.get(articleIndex);
		Text title = new Text(article.getTitle() + "\n");
		Text authors = new Text("\t" + parseAuthorsList(article.getAuthors())+ "\n");
		Text desc = new Text("Summary : " + article.getSummary());
		return new HBox(10, new TextFlow(title, authors, desc));
	}
}