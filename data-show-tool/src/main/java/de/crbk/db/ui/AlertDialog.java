package de.crbk.db.ui;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AlertDialog
{

    private static double xOffset = 0;

    private static double yOffset = 0;

    public static void startDialog(AlertType type, String header, String content)
    {
        startDialog(type, header, content, null);
    }

    public static void startDialog(AlertType type, String header, String content, Exception exception)
    {
        Platform.runLater(() -> {
            Alert dlg = new Alert(type);
            dlg.setHeaderText(header.toUpperCase());
            dlg.setContentText(content);

            dlg.getDialogPane().setOnMousePressed(event -> {
                xOffset = dlg.getX() - event.getScreenX();
                yOffset = dlg.getY() - event.getScreenY();
            });

            dlg.getDialogPane().setOnMouseDragged(event -> {
                dlg.setX(event.getScreenX() + xOffset);
                dlg.setY(event.getScreenY() + yOffset);
            });

            if (exception != null)
            {
                addStackTrace(dlg, exception);
            }
            dlg.show();
        });

    }

    private static void addStackTrace(Alert dlg, Exception exception)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        dlg.getDialogPane().setExpandableContent(expContent);
    }
}
