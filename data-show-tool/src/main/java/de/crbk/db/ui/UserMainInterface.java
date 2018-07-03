package de.crbk.db.ui;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import de.crbk.db.common.Constants;
import de.crbk.db.common.DatabaseUserTables;
import de.crbk.db.common.DatabaseUsers;
import de.crbk.db.controller.UniversityData;
import de.crbk.db.exceptions.DataToolException;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnChange;

    private String selectedView;

    private TableView<Map<String, String>> shownTable;

    @Override
    public void start(Stage primaryStage)
        throws Exception
    {
        Parent root = FXMLLoader.load(new File(Constants.MAINFRAME_FXML).toURI().toURL());

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

        disableButtons();

        try
        {
            if (shownTable != null)
            {
                LOG.info("Remove old table view.");
                this.tableScrollPane.setContent(null);
            }

            UniversityData.getInstance().createDatabaseConnection();
            if (!UniversityData.getInstance().setUserForInput(identificationField.getText()))
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
     * disable buttons
     */
    private void disableButtons()
    {
        this.btnAdd.setDisable(true);
        this.btnDelete.setDisable(true);
        this.btnChange.setDisable(true);
    }

    /**
     * view selected
     */
    @FXML
    public void viewSelected()
    {
        LOG.info("Following view was selected: " + resultListView.selectionModelProperty().getName());
        selectedView = resultListView.getSelectionModel().getSelectedItem();

        disableButtons();

        String query = "";
        if (!UniversityData.getInstance().getCurrentUser().equalsIgnoreCase(DatabaseUsers.ADMIN_EMPLOYEE))
        {
            query = "SELECT * FROM " + selectedView + " WHERE " + DatabaseUserTables.ID_COLUMN + " = "
                    + UniversityData.getInstance().getCurrentIdentificationNumber();
        }
        else
        {
            query = "SELECT * FROM " + selectedView;
        }
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

                if (columnName.startsWith(DatabaseUserTables.ID_COLUMN)
                        && !UniversityData.getInstance().getCurrentUser().equals(DatabaseUsers.ADMIN_EMPLOYEE))
                {
                    LOG.debug("Set ID column invisible");
                    currColumnView.setVisible(false);
                }

                currColumnView.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(columnName)));

                currColumnView.setCellFactory(new CustomCellFactory());

                shownTable.getColumns().add(currColumnView);
            }

            // set selection mode to to single mode --> multi selection is now disabled
            shownTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            shownTable.autosize();
            shownTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            this.tableScrollPane.setContent(shownTable);

            enableButtons();
        }
        catch (SQLException e)
        {
            AlertDialog.startDialog(AlertType.ERROR, "Connot open view.",
                    "Cannot load follwing view from database. View: " + selectedView, e);
            LOG.error("Error while loading view.", e);
        }
    }

    /**
     * enable the right buttons
     */
    private void enableButtons()
    {
        if (UniversityData.getInstance().getCurrentUser().equalsIgnoreCase(DatabaseUsers.ADMIN_EMPLOYEE))
        {
            LOG.debug("Set all button on enabled.");
            this.btnAdd.setDisable(false);
            this.btnDelete.setDisable(false);
            this.btnChange.setDisable(false);
        }
        else if (selectedView.equalsIgnoreCase(DatabaseUserTables.VIEW_ASSISTANT)
                || selectedView.equalsIgnoreCase(DatabaseUserTables.VIEW_STUDENT)
                || selectedView.equalsIgnoreCase(DatabaseUserTables.VIEW_PROFESSOR))
        {
            LOG.debug("Set change button on enabled.");
            this.btnChange.setDisable(false);
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
        startEditDialog(true, shownTable.getSelectionModel().getSelectedItem());
    }

    /**
     * starts the edit dialog
     * 
     * @param isUpdate
     *            set if the started dialog is for insert or not
     */
    private void startEditDialog(boolean isUpdate, Map<String, String> values)
    {
        Stage stage = new Stage();
        try
        {
            FXMLLoader loader = new FXMLLoader(new File(Constants.EDITFRAME_FXML).toURI().toURL());

            UserEditDialog controller = new UserEditDialog();
            controller.setSelectedRow(values);
            controller.setView(selectedView);
            controller.setUpdate(isUpdate);
            controller.setMainInterface(this);
            loader.setController(controller);

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
        LOG.info("Insert was clicked. Start dialog.");
        if (shownTable == null)
        {
            AlertDialog.startDialog(AlertType.INFORMATION, "An view must be selected.", "Select a view.");
            return;
        }
        startEditDialog(false, createColumnMap());
    }

    /**
     * create a map with all colunms but with empty values
     * 
     * @return
     */
    private Map<String, String> createColumnMap()
    {
        Map<String, String> result = new HashMap<>();
        for (TableColumn<Map<String, String>, ?> curr : shownTable.getColumns())
        {
            result.put(curr.getText(), null);
        }
        return result;
    }

    /**
     * executed if delete button was clicked
     */
    @FXML
    private void deleteClick()
    {
        LOG.info("Delete clicked.");
        if (shownTable == null || shownTable.getSelectionModel().isEmpty()
                || shownTable.getSelectionModel().getSelectedItem() == null)
        {
            LOG.info("No row was selected.");
            AlertDialog.startDialog(AlertType.INFORMATION, "Please select a row to delete it.", "No row was selected");
            return;
        }

        Map<String, String> selectedRow = shownTable.getSelectionModel().getSelectedItem();

        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(selectedView);
        query.append(" WHERE ");

        AtomicBoolean first = new AtomicBoolean(true);
        selectedRow.forEach((key, value) ->
        {
            if (key.startsWith(DatabaseUserTables.ID_COLUMN))
            {
                if (first.get())
                {
                    first.set(false);
                }
                else
                {
                    query.append(" AND ");
                }
                query.append(key);
                query.append(" = ");
                query.append(value);
            }
        });

        try
        {
            UniversityData.getInstance().executeSqlStatement(query.toString());
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            AlertDialog.startDialog(AlertType.ERROR, "Error while delete row.",
                    "Please try again or see stack trace for details.", e);
        }

        viewSelected();
    }
}