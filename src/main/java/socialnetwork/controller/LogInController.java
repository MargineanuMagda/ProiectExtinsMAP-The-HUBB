package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;

import java.awt.*;

public class LogInController {
    ServiceDbNetwork serv;
    Stage stage;

    @FXML
    Button logIn;
    @FXML
    Button newUser;

    public void handleLogIn(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(getClass().getResource("/views/menuView.fxml"));
            AnchorPane root = (AnchorPane) loader.load();

           /* //Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Social network");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            UserController userCtrl = loader.getController();
            userCtrl.setService(serv, (Utilizator) serv.getUser(log.getIdGenerat()));
            stage.close();
            dialogStage.show();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void setService(ServiceDbNetwork serv, Stage primaryStage) {
        this.serv=serv;
        stage=primaryStage;
    }
}
