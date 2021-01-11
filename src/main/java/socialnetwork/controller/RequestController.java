package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.service.ServiceDbNetwork;
import javafx.scene.control.Label;

import java.awt.*;

public class RequestController {
    ServiceDbNetwork serv;
    FriendRequest request;
    Stage mainStage;
    @FXML
    Button appBtn;
    @FXML
    Button decBtn;

    @FXML
    Label from;
    @FXML
    Label status;
    @FXML
    Label date;

    public void setService(ServiceDbNetwork serv, Stage stage, FriendRequest friendRequest) {
        this.serv=serv; request=friendRequest; mainStage=stage;
        date.setText("date:\t"+friendRequest.getData().toString());
        status.setText("status:\t"+friendRequest.getStatus());
        from.setText("from:\t"+serv.getUser(friendRequest.getId().getLeft()).toString());
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
