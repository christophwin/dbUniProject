package de.crbk.db.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.crbk.db.common.DatabaseRoles;
import de.crbk.db.common.DatabaseUserTables;
import de.crbk.db.controller.UniversityData;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserEditDialog implements Initializable
{
    private static final Logger LOG = Logger.getLogger(UserEditDialog.class);

    private UserMainInterface mainInterface;

    private String view;

    private boolean isUpdate = false;

    private Map<String, String> selectedRow;

    @FXML
    private VBox fieldsVBox;

    /**
     * method for initialize
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        LOG.info("Start creation of dynamic view for current dataset.");
        if (this.selectedRow == null)
        {
            AlertDialog.startDialog(AlertType.ERROR, "The selected row is not valid.",
                    "Plese close this view and try again.");
            return;
        }

        for (Map.Entry<String, String> currCol : selectedRow.entrySet())
        {
            LOG.debug("Current collum for creation: " + currCol);
            if (currCol.getKey().startsWith(DatabaseUserTables.ID_COLUMN)
                    && !UniversityData.getInstance().getCurrentRole().equals(DatabaseRoles.ADMIN_EMPLOYEE) && isUpdate)
            {
                LOG.debug("ID will not be shown for this user.");
                continue;
            }

            fieldsVBox.getChildren().add(new Label(currCol.getKey() + ":"));

            TextField txtField = new TextField();
            txtField.setId(currCol.getKey());
            txtField.setText(currCol.getValue());

            fieldsVBox.getChildren().add(txtField);

        }
    }

    /**
     * executed if save was clicked
     * 
     * @param event
     */
    @FXML
    private void saveClicked(ActionEvent event)
    {
        LOG.info("Save Button was clicked.");
        FilteredList<Node> textFields = fieldsVBox.getChildren().filtered(curr -> curr instanceof TextField);

        try
        {
            if (isUpdate)
            {
                LOG.debug("Create an update statement.");
                Map<String, String> columnsToUpdate = getUpdateValues(textFields);
                Map<String, String> identificationValues = getIdentificationvalues();
                LOG.debug("Update columns: " + columnsToUpdate);
                LOG.debug("Identification columns: " + identificationValues);
                if (!columnsToUpdate.isEmpty())
                {
                    UniversityData.getInstance()
                            .executeSqlStatement(createUpdateStatement(columnsToUpdate, identificationValues));
                }
                else
                {
                    LOG.info("No update needed.");
                }
            }
            else
            {
                // TODO
            }
        }
        catch (Exception e)
        {
            LOG.error(e.getMessage(), e);
            AlertDialog.startDialog(AlertType.ERROR, "Error while saving values.",
                    "Please try again. See stack trace for more informations.", e);
        }
        mainInterface.viewSelected();
        ((Stage) ((Node) event.getTarget()).getScene().getWindow()).close();
    }

    /**
     * get the identification columns with values
     * 
     * @return
     */
    private Map<String, String> getIdentificationvalues()
    {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> currEntry : selectedRow.entrySet())
        {
            if (currEntry.getKey().startsWith(DatabaseUserTables.ID_COLUMN))
            {
                result.put(currEntry.getKey(), currEntry.getValue());
            }
        }
        return result;
    }

    /**
     * creates update statement for given columns
     * 
     * @param columnsToUpdate
     *            columns for update
     * @param identificationColumns
     *            columns for identification
     * @return
     */
    private String createUpdateStatement(Map<String, String> columnsToUpdate, Map<String, String> identificationColumns)
    {
        LOG.debug("Create update statement.");
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(view).append(" SET ");
        sb.append(getTupel(columnsToUpdate, ","));
        sb.append(" WHERE ");
        sb.append(getTupel(identificationColumns, "AND"));
        return sb.toString();
    }

    /**
     * create tupels for SQL statement
     * 
     * @param tupels
     *            columns with value
     * @param seperator
     *            sepataor for mutiple tupels
     * @return
     */
    private String getTupel(Map<String, String> tupels, String seperator)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> currEntry : tupels.entrySet())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                sb.append(" " + seperator + " ");
            }
            sb.append(currEntry.getKey());
            sb.append(" = '");
            sb.append(currEntry.getValue());
            sb.append("'");
        }

        return sb.toString();
    }

    /**
     * get the columns that must be updated
     * 
     * @param textFields
     *            text fields from dialog
     * @return
     */
    private Map<String, String> getUpdateValues(FilteredList<Node> textFields)
    {
        // key -> column ; value -> value
        Map<String, String> result = new HashMap<>();

        for (Node curr : textFields)
        {
            String columnName = ((TextField) curr).getId();
            String currValue = ((TextField) curr).getText();

            if (currValue.equals(selectedRow.get(columnName)))
            {
                LOG.debug("Column '" + columnName + "' must not be updated.");
            }
            else
            {
                LOG.debug("Column '" + columnName + "' must be updated. New value: " + currValue);
                result.put(columnName, currValue);
            }

        }

        return result;
    }

    public void setSelectedRow(Map<String, String> selectedRow)
    {
        this.selectedRow = selectedRow;
    }

    public void setUpdate(boolean isUpdate)
    {
        this.isUpdate = isUpdate;
    }

    public void setView(String view)
    {
        this.view = view;
    }

    public void setMainInterface(UserMainInterface mainInterface)
    {
        this.mainInterface = mainInterface;
    }
}
