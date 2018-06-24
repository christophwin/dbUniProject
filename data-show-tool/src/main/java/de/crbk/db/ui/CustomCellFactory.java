package de.crbk.db.ui;

import java.util.Map;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * helper class for table view
 * 
 * @author C. Winter
 *
 */
public class CustomCellFactory
        implements Callback<TableColumn<Map<String, String>, String>, TableCell<Map<String, String>, String>>
{

    @Override
    public TableCell<Map<String, String>, String> call(TableColumn<Map<String, String>, String> param)
    {
        return new TextFieldTableCell<Map<String, String>, String>(new StringConverter<String>()
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
    }
}
