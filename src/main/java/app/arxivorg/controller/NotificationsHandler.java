package app.arxivorg.controller;

import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationsHandler {

    private static Notifications buildNotification(String title, String text) {
        return Notifications.create()
                .title(title)
                .text(text)
                .darkStyle()
                .position(Pos.TOP_RIGHT)
                .hideAfter(Duration.seconds(5));
    }

    public static void showWarning(String title, String text) {
        buildNotification(title, text).showWarning();
    }

    public static void showConfirmation(String title, String text) {
        buildNotification(title, text).showInformation();
    }
}
