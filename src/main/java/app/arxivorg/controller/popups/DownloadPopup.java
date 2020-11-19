package app.arxivorg.controller.popups;

import app.arxivorg.controller.DownloadOption;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import javafx.event.Event;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DownloadPopup implements Initializable {

    @FXML private TextField pathField;
    @FXML private ComboBox<String> optionField;

    private static Stage window;

    private static String path;
    private static String option;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initWindow();
        path = "";
        optionField.getSelectionModel().selectFirst();
        option = optionField.getSelectionModel().getSelectedItem();
    }

    public static List<String> getDownloadOptions(String title) {
        try {
            display(title);
            return path.equals("") ? null : List.of(path, String.valueOf(option));
        }
        catch (Exception e) {
            handleException(e);
            return null;
        }
    }

    private void initWindow() {
        window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setResizable(false);
        window.setFullScreen(false);
        window.setMaximized(false);
    }

    private static void display(String title) throws Exception {
            Parent layout = FXMLLoader.load((DownloadPopup.class)
                    .getResource("/app/arxivorg/view/popups/DownloadPopup.fxml"));
            window.setScene(new Scene(layout));
            window.setTitle(title);
            window.showAndWait();
    }


    private static void handleException(Exception e) {
        if (e instanceof IllegalStateException) return;
        e.printStackTrace();
        ErrorPopup.display("Unable to show download window. Please retry.");
    }

    @FXML
    private void setOptions() {
        path = pathField.getText();
        if (path.equals("")) return;
        option = optionField.getSelectionModel().getSelectedItem();
        close();
    }

    @FXML
    private void cancel() {
        path = "";
        window.close();
    }

    @FXML
    private void close() {
        window.close();
    }
}
