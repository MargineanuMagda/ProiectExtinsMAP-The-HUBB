package socialnetwork.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessageAlert {



     static void showMessage(Stage owner, Alert.AlertType type, String header, String text){
        Alert message=new Alert(type);

        message.getDialogPane().getStylesheets().add(MessageAlert.class.getResource("/css/style.css").toExternalForm());

       message.setHeaderText(header);
        message.setContentText(text);
        message.initOwner(owner);
        message.showAndWait();
    }



    static void showErrorMessage(Stage owner, String text){
        Alert message=new Alert(Alert.AlertType.ERROR);

        message.getDialogPane().getStylesheets().add(MessageAlert.class.getResource("/css/style.css").toExternalForm());

        message.initOwner(owner);
        message.initStyle(StageStyle.UTILITY);
        message.setTitle("Mesaj eroare");
        message.setContentText(text);
        message.showAndWait();
    }

}
