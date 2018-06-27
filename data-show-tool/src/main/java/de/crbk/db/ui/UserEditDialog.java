package de.crbk.db.ui;

import java.awt.List;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import de.crbk.db.common.DatabaseRoles;
import de.crbk.db.common.DatabaseUserTables;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class UserEditDialog
    implements Initializable
{
    private static final Logger LOG = Logger.getLogger(UserEditDialog.class);

    private String selectedView;

    private String currentRole;

    private Map<String, String> selectedRow;

    private Map<String, String> changedValues = new HashMap<>();

    @FXML
    private VBox fieldsVBox;

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
            if (currCol.getKey().equals(DatabaseUserTables.ID_COLUMN)
                && !currentRole.equals(DatabaseRoles.ADMIN_EMPLOYEE))
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

    public void setSelectedRow(Map<String, String> selectedRow)
    {
        this.selectedRow = selectedRow;
    }

    public void setSelectedView(String selectedView)
    {
        this.selectedView = selectedView;
    }

    public void setCurrentRole(String selectedRole)
    {
        this.currentRole = selectedRole;
    }

    @FXML
    private void saveClicked(ActionEvent event)
    {
        LOG.info("Save Button was clicked.");
        FilteredList<Node> textFields = fieldsVBox.getChildren().filtered(curr -> curr instanceof TextField);

    }

}
