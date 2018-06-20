package de.crbk.db.ui;

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

            TableView<Map<String, String>> table = new TableView<>(generateValueForView(result));

            for (int i = 1; i <= result.getMetaData().getColumnCount(); i++)
            {
                String columnName = result.getMetaData().getColumnName(i);
                LOG.debug("Create column view for following column: " + columnName);

                TableColumn<Map<String, String>, String> currColumnView = new TableColumn<>(columnName);
                currColumnView.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(columnName)));

                currColumnView.setCellFactory(
                        new Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>()
                        {
                            @Override
                            public TableCell<Map<String, String>, String> call(
                                    javafx.scene.control.TableColumn<Map<String, String>, String> param)
                            {
                                return new TextFieldTableCell<Map<String, String>, String>(
                                        new StringConverter<String>()
                                        {

                                            @Override
                                            public String fromString(String string)
                                            {
                                                return string;
                                            }

                                            @Override
                                            public String toString(String object)
                                            {
                                                return object;
                                            }

                                        });
                            };
                        });

                table.getColumns().add(currColumnView);
            }

            this.tableScrollPane.setContent(table);

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

}
