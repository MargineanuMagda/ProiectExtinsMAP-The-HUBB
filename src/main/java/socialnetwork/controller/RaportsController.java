package socialnetwork.controller;

import com.itextpdf.text.DocumentException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class RaportsController {
    ServiceDbNetwork serv;
    Utilizator mainUser;
    Stage mainStage;
    @FXML
    DatePicker date1;
    @FXML
    DatePicker date2;
    @FXML
    DatePicker date3;
    @FXML
    DatePicker date4;
    @FXML
    Label lbl3;
    @FXML
    TextField filename1;
    @FXML
    TextField filename2;
    @FXML
    ComboBox<Utilizator> comboF;

    public void setService(ServiceDbNetwork serv, Stage dialogStage, Utilizator user) {
        this.serv = serv;
        this.mainUser = user;
        this.mainStage=dialogStage;
        lbl3.setText("HELLO "+ mainUser.toString()+" !");
        ObservableList<Utilizator> friends = FXCollections.observableArrayList();
        friends.setAll(new ArrayList<>(serv.getUserFriends1(mainUser.getId())));
        comboF.setItems(friends);
    }

    public void handleGenerateRap1(ActionEvent actionEvent) {
        if ( date1.getValue()!= null && date2.getValue() != null && date1.getValue().isBefore(date2.getValue()) && filename1.getText()!= null)
        {
            try {
                serv.raport1(mainUser,date1.getValue(),date2.getValue(),filename1.getText());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFO","Raport generated succesfully!");
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }
        }else
            MessageAlert.showErrorMessage(null,"Pick the dates in order please!");
    }

    public void handleGenerateRap2(ActionEvent actionEvent) {
        if ( date3.getValue()!= null && date4.getValue() != null && date3.getValue().isBefore(date4.getValue()) && filename2.getText()!= null && comboF.getValue()!=null)
        {
            try {
                serv.raport2(mainUser,comboF.getValue(),date3.getValue(),date4.getValue(),filename2.getText());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFO","Raport generated succesfully!");
            } catch (FileNotFoundException | DocumentException e) {
                e.printStackTrace();
            }
        }else
            MessageAlert.showErrorMessage(null,"Pick the dates in order please!");
    }
}
