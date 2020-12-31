package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.LogIn;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;

import java.awt.*;

public class LogController {
    ServiceDbNetwork serv;
    Stage mainStage;

   @FXML
   TextField uNume;
    @FXML
    PasswordField passwd;

    public void setService(ServiceDbNetwork serv, Stage primaryStage) {
        this.serv=serv;
        mainStage=primaryStage;
    }

    public Utilizator handleLog(ActionEvent actionEvent) {
        try {

            String username = uNume.getText();
            String passowrd = passwd.getText();

            LogIn log = serv.findLog(new Tuple<>(username,passowrd));

            if( log != null){
                try {
                    FXMLLoader loader = new FXMLLoader();

                    loader.setLocation(getClass().getResource("/views/menuPageView.fxml"));
                    AnchorPane root = (AnchorPane) loader.load();

                    //Stage
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Social network");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    UserController userCtrl = loader.getController();
                    userCtrl.setService(serv,(Utilizator)serv.getUser(log.getIdGenerat()),dialogStage);

                    dialogStage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
            else
                MessageAlert.showErrorMessage(null,"Invalid username/password!");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void handleNewUser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(getClass().getResource("/views/newUserView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

            //Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("New User");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            NewUserController newUserCtrl = loader.getController();
            newUserCtrl.setService(serv,dialogStage);

            dialogStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
