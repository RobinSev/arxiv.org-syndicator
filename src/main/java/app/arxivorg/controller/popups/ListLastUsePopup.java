package app.arxivorg.controller.popups;

import app.arxivorg.controller.ArxivOrgController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class ListLastUsePopup implements Initializable {

    @FXML
    private ComboBox<Integer> numberChoice;
    @FXML
    private DatePicker startDateChoice;

    private static Stage window;
    private static int numberOfArticles = 0;
    private static LocalDate startDate = LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initWindow();
        initDatePicker();
        initNumberChoiceMenu();
    }

    private void initNumberChoiceMenu() {
        ObservableList<Integer> items = FXCollections.observableArrayList();
        for (int i = 1; i <= 100; i++) {
            items.add(i);
        }
        numberChoice.setItems(items);
        numberChoice.getSelectionModel().selectFirst();
    }

    public static boolean show(String title) {
        try {
            display(title);
        } catch (Exception e) {
            handleException(e);
            return false;
        }
        return true;
    }

    public static int getNumberOfArticles() {
        return numberOfArticles;
    }

    public static LocalDate getStartDate() {
        return startDate;
    }

    private static void initWindow() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(true);
        window.setFullScreen(false);
        window.setMaximized(false);
    }

    private void initDatePicker() {
        startDateChoice.setShowWeekNumbers(false);
        startDateChoice.setDayCellFactory(getDateCellFactory());
    }

    private Callback<DatePicker, DateCell> getDateCellFactory() {
        return new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {

                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item.isAfter(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #EEEEEE;");
                        }
                    }
                };
            }
        };
    }

    private static void display(String title) throws IOException {
        Parent layout = FXMLLoader.load(ListLastUsePopup.class
                .getResource("/app/arxivorg/view/popups/ListSinceLastUsePopup.fxml"));
        window.setTitle(title);
        window.setScene(new Scene(layout));
        window.showAndWait();
    }

    @FXML
    private void setDatas() {
        if (numberChoice.getValue() == null) return;
        numberOfArticles = numberChoice.getValue() == null ? -1 : numberChoice.getValue();
        startDate = startDateChoice.getValue();
        close();
    }

    @FXML
    private void close() {
        window.close();
    }

    private static void handleException(Exception e) {
        e.printStackTrace();
        ErrorPopup.display();
    }
}
