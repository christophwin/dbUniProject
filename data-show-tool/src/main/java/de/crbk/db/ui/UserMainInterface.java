package de.crbk.db.ui;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.crbk.db.common.Constants;
import de.crbk.db.controller.UniversityData;
import de.crbk.db.exceptions.DataToolException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Implementation of the main user interface
 */
public class UserMainInterface extends Application
{
    private static final Logger LOG = Logger.getLogger(UserMainInterface.class);

    @FXML
    private TextField identificationField;

    @FXML
    private ListView<String> resultListView;

    @FXML
    private ScrollPane tableScrollPane;

    private String selectedView;

    private TableView<Map<String, String>> shownTable;

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

    /**
     * search executed
     */
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
            if (!UniversityData.getInstance().setRoleForInput(identificationField.getText()))
            {
                return;
            }

            ObservableList<String> viewsForRole = FXCollections
                    .observableArrayList(UniversityData.getInstance().getAllViews());
            this.resultListView.setItems(viewsForRole);

        }
        catch (DataToolException e)
        {
            LOG.error(e.getMessage(), e);
            AlertDialog.startDialog(AlertType.ERROR, "An error occur while database connection.",
                    "See stack trace for details.", e);
        }
    }

    /**
     * view selected
     */
    @FXML
    private void viewSelected()
    {
        LOG.info("Following view was selected: " + resultListView.selectionModelProperty().getName());
        selectedView = resultListView.getSelectionModel().getSelectedItem();

        String query = "SELECT * FROM " + selectedView;
        LOG.debug("Exceute SQL-query: " + query);

        try (PreparedStatement stmt = UniversityData.getInstance().getDatabaseConnection().prepareStatement(query);
                ResultSet result = stmt.executeQuery())
        {

            shownTable = new TableView<>(generateValueForView(result));

            for (int i = 1; i <= result.getMetaData().getColumnCount(); i++)
            {
                String columnName = result.getMetaData().getColumnName(i);
                LOG.debug("Create column view for following column: " + columnName);

                TableColumn<Map<String, String>, String> currColumnView = new TableColumn<>(columnName);
                currColumnView.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(columnName)));

                currColumnView.setCellFactory(new CustomCellFactory());

                shownTable.getColumns().add(currColumnView);
            }

            // set selection mode to to single mode --> multi selection is now disabled
            shownTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

            this.tableScrollPane.setContent(shownTable);

        }
        catch (SQLException e)
        {
            AlertDialog.startDialog(AlertType.ERROR, "Connot open view.",
                    "Cannot load follwing view from database. View: " + selectedView, e);
            LOG.error("Error while loading view.", e);
        }
    }

    /**
     * generates a list which include a map for every resultset row
     * 
     * @param resultSet
     *            resultset with all values
     * @param columnNames
     *            column names like in the resultSet
     * @return
     * @throws SQLException
     */
    private ObservableList<Map<String, String>> generateValueForView(ResultSet resultSet)
        throws SQLException
    {
        LOG.debug("Create values for table view.");

        ObservableList<Map<String, String>> values = FXCollections.observableArrayList();

        resultSet.beforeFirst();
        while (resultSet.next())
        {
            Map<String, String> currRow = new HashMap<>();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
            {
                currRow.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
            }
            values.add(currRow);
        }

        LOG.debug("Values: " + values);
        return values;
    }

    /**
     * executed if button for change was clicked
     */
    @FXML
    private void changeClick()
    {
        LOG.info("Change was clicked. Call dialog.");

        if (shownTable == null || shownTable.getSelectionModel().isEmpty()
                || shownTable.getSelectionModel().getSelectedItem() == null)
        {
            LOG.info("No row was selected.");
            AlertDialog.startDialog(AlertType.INFORMATION, "Please select a row to edit it", "No row was selected");
            return;
        }
        Stage stage = new Stage();

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(Constants.EDITFRAME_FXML));

        UserEditDialog controller = new UserEditDialog();
        controller.setSelectedRow(shownTable.getSelectionModel().getSelectedItem());
        controller.setSelectedView(selectedView);
        loader.setController(controller);

        try
        {
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Edit data");
            stage.show();
        }
        catch (IOException e)
        {
            LOG.error(e.getMessage(), e);
            AlertDialog.startDialog(AlertType.ERROR, "Error while starting dialog.",
                    "Look at stack trace for more information.", e);
        }
    }

    @FXML
    private void insertClick()
    {
        
    }
    
    @FXML
    private void deleteClick()
    {
        
    }
}
