package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;
import socialnetwork.service.ServiceException;

import java.io.IOException;

public class NewUserController {
    ServiceDbNetwork serv;
    Stage st;
    @FXML
    TextField fName;
    @FXML
    TextField lName;
    @FXML
    PasswordField id;

    public void setService(ServiceDbNetwork serv,Stage stage){
        this.serv=serv;
        this.st=stage;
    }

    public void hanndleAddUser(ActionEvent actionEvent) {
        String firstname = fName.getText();
        String lastname = lName.getText();
        Long id1=0L;
        try{
             id1 = Long.parseLong(id.getText());
            Utilizator u = new Utilizator(firstname,lastname);
            u.setId(id1);
            serv.addUtilizator(u);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFORMATION","User added succesfully!");
            st.close();
        }catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }



    }
}
