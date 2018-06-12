package de.crbk.db.ui;

import org.apache.log4j.Logger;

import de.crbk.db.common.Constants;
import de.crbk.db.controller.UniversityData;
import de.crbk.db.exceptions.DataToolException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Implementation of the main user interface
 */
public class UserMainInterface
    extends Application
{
    private static final Logger LOG = Logger.getLogger(UserMainInterface.class);

    @FXML
    private TextField identificationField;

    @FXML
    private ListView<String> resultListView;

    @FXML
    private ScrollPane tableScrollPane;

    @Override
    public void start(Stage primaryStage)
        throws Exception
    {
        Parent root = FXMLLoader.load(this.getClass().getResource(Constants.MAINFRAME_FXML));

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("University data");
        primaryStage.show();

    }

    @FXML
    private void executeSearch()
    {
        LOG.info("Search executed with following value: " + identificationField.getText());
        if (identificationField.getText() == null || identificationField.getText().isEmpty())
        {
            LOG.info("No data input.");
            AlertDialog.startDialog(AlertType.WARNING, "No input given.",
                                    "The identification number has to be filled.");
            return;
        }
        try
        {
            UniversityData.getInstance().createDatabaseConnection();
        }
        catch (DataToolException e)
        {
            LOG.error(e.getMessage(), e);
            AlertDialog.startDialog(AlertType.ERROR, "An error occur while database connection.",
                                    "See stack trace for details.", e);
        }
    }

}
