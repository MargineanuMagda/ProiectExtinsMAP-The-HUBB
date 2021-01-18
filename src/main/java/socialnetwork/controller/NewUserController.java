package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.LogIn;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;
import socialnetwork.service.ServiceException;
import socialnetwork.utils.Utils;

import java.io.IOException;
import java.util.Arrays;

public class NewUserController {
    ServiceDbNetwork serv;
    Stage st;
    @FXML
    TextField fName;
    @FXML
    TextField lName;
    @FXML
    TextField uName;
    @FXML
    PasswordField pass;
    @FXML
    PasswordField passwC;

    public void setService(ServiceDbNetwork serv,Stage stage){
        this.serv=serv;
        this.st=stage;
    }

    public void hanndleAddUser(ActionEvent actionEvent) {
        String firstname = fName.getText();
        String lastname = lName.getText();
        String username = uName.getText();
        String passwd = Utils.hash(pass.getText());
        String passwdC = Utils.hash(passwC.getText());
        if(passwd.equals(passwdC)){
            try{
                LogIn log = new LogIn(0L);
                log.setId(new Tuple<>(username,passwd));
                serv.addLog(log);

                Long id1 = serv.findLogID(username,passwd);
                Utilizator u = new Utilizator(firstname,lastname);
                u.setBio(Arrays.asList(" "," "," "," "," "," "));
                u.setId(id1);
                serv.addUtilizator(u);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFORMATION","User added succesfully!");
                st.close();
            }catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }else{
            MessageAlert.showErrorMessage(null,"Both passwords should be the same!!");
        }




    }
}
