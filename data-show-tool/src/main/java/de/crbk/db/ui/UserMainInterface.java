package de.crbk.db.ui;

import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.crbk.db.common.Constants;
import de.crbk.db.controller.UniversityData;
import de.crbk.db.exceptions.DataToolException;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArrayBase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
            UniversityData.getInstance().setRoleForInput(identificationField.getText());
            
            ObservableList<String> viewsForRole = FXCollections.observableArrayList(UniversityData.getInstance().getAllViews());
            this.resultListView.setItems(viewsForRole);
            
        }
        catch (DataToolException e)
        {
            LOG.error(e.getMessage(), e);
            AlertDialog.startDialog(AlertType.ERROR, "An error occur while database connection.",
                                    "See stack trace for details.", e);
        }
    }



}
