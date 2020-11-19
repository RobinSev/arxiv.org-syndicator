package app.arxivorg.views;

import app.arxivorg.controller.ArxivOrgController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;


@ExtendWith(ApplicationExtension.class)
public class ViewsTest {

	/**
	 * A method is the first one executed
	 * and it loads the FXML file before executing the unit tests.
	 * */
	@Start
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(ArxivOrgController.class.getResource("/app/arxivorg/view/arxivorg.fxml"));
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}

	/**
	 * At the first execution of the page this test if the article list is empty.
	 *
	 * */
	@Test
	void ArticlesListEmpty(FxRobot robot) {
		ListView listView = robot.lookup("#listArticles").queryListView();
		Assertions.assertThat(listView.getItems().size() == 0);
	}

	/**
	 * A method tests whether or not the category filters are
	 * instantiated during the first execution of the page.
	 *
	 * */
	@Test
	void filterCategory(FxRobot robot) {
		int categorySize = robot.lookup("#categoryField").queryComboBox().getItems().size();
		Assertions.assertThat(categorySize > 0);
	}


}//class
