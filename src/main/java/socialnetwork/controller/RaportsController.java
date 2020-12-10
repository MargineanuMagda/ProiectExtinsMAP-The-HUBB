package socialnetwork.controller;

import com.itextpdf.text.DocumentException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;

import java.io.FileNotFoundException;
import java.time.LocalDate;

public class RaportsController {
    ServiceDbNetwork serv;
    Utilizator mainUser;
    Stage mainStage;
    @FXML
    DatePicker date1;
    @FXML
    DatePicker date2;
    @FXML
    Label lbl;
    @FXML
    TextField filename1;

    public void setService(ServiceDbNetwork serv, Stage dialogStage, Utilizator user) {
        this.serv = serv;
        this.mainUser = user;
        this.mainStage=dialogStage;
        lbl.setText("HELLO "+ mainUser.toString()+" !");
    }

    public void handleGenerateRap1(ActionEvent actionEvent) {
        if ( date1.getValue()!= null && date2.getValue() != null && date1.getValue().isBefore(date2.getValue()) && filename1.getText()!= null)
        {
            try {
                serv.raport1(mainUser,date1.getValue(),date2.getValue(),filename1.getText());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFO","Raport generated succesfully!");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }else
            MessageAlert.showErrorMessage(null,"Pick the dates in order please!");
    }
}
