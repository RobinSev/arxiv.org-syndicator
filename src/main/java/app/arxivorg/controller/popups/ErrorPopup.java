package app.arxivorg.controller.popups;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;


import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

/**
 * This class representing an error pop-up window with customizable error message.
 * The window is only build in Java, without any fxml file, to avoid the case of an IO exception
 * caused by the loading of the file.
 *
 * This window being generic, all the method of the class are statics.
 */
public class ErrorPopup {

    /**
     * The message displayed if no message is specified in the display method.
     */
    private static final String DEFAULT_MESSAGE =
            "An error occurred during the process, making this service unavailable.\nPlease, try it later.";

    /**
     * Constructor has private access to avoid
     * any instantiation of this class.
     */
    private ErrorPopup() {}

    /**
     * Constructs and show a window based on the JavaFX's 'Alert' window model
     * by specifying the header text and the content text explaining the error
     * (stylized as specified in getStylizedLabel method and contained by a HBox).
     *
     *
     * @param message - The message to display into the pop-up.
     */
    public static void display(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setHeaderText("Oops, an error occurred :/");
        alert.getDialogPane().setContent(getHBox(375.0,0.0, getStylizedLabel(message)));
        alert.showAndWait();
    }


    /**
     * Display the error pop-up by calling the display method below
     * with the default message as parameter.
     */
    public static void display() {
        display(DEFAULT_MESSAGE);
    }


    /**
     * Returns the stylized label containing the specified message.
     * The text returned label appears in black with the font
     * Helvetica Neue, and size 15.0.
     *
     * @param message - The message contained by the returned label.
     *
     * @return the stylized label containing the specified message.
     */
    private static Label getStylizedLabel(String message) {
        Label label = new Label(message);
        label.setTextFill(Paint.valueOf("#DF0101"));
        label.setFont(new Font("Helvetica Neue", 15.0));
        label.setStyle("-fx-font-weight: bold");
        return label;
    }

    /**
     * Returns the HBox configured with the specified {@code prefHeight}
     * and containing the specified {@code children}.
     * The returned HBox also aligns these children to the center and
     * have a padding of 13 on all faces.
     *
     * @param minHeight - The default height of the HBox.
     * @param children - The nodes contained by the returned HBox.
     *
     * @return the HBox configured with the specified height and children.
     */
    private static HBox getHBox(double minWidth, double minHeight, Node... children) {
        HBox hBox = new HBox(children);
        hBox.setAlignment(Pos.CENTER);
        hBox.setMinWidth(minWidth);
        hBox.setMinHeight(minHeight);
        hBox.setPadding(new Insets(13));
        return hBox;
    }
}