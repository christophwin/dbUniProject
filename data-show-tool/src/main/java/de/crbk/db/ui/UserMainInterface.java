package de.crbk.db.ui;

import de.crbk.db.common.Constants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Implementation of the main unser interface
 */
public class UserMainInterface extends Application
{

    @Override
    public void start(Stage primaryStage)
        throws Exception
    {
        Parent root = FXMLLoader.load(this.getClass().getResource(Constants.MAINFRAME_FXML));

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        scene.setRoot(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
