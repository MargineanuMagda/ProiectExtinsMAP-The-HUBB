package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.service.ServiceDbNetwork;

import java.awt.*;

public class RequestController {
    ServiceDbNetwork serv;
    FriendRequest request;
    Stage mainStage;
    @FXML
    Button appBtn;
    @FXML
    Button decBtn;

    public void setService(ServiceDbNetwork serv, Stage stage, FriendRequest friendRequest) {
        this.serv=serv; request=friendRequest; mainStage=stage;
    }

    public void handleAccept(ActionEvent actionEvent) {

        request.setStatus("approved");
        serv.changeStatus(request);
        mainStage.close();
    }

    public void handleDecline(ActionEvent actionEvent) {
        request.setStatus("rejected");
        serv.changeStatus(request);
        mainStage.close();
    }
}
