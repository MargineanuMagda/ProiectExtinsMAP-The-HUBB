package socialnetwork.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;

import java.time.LocalDateTime;
import java.util.List;

public class SentRequests {
    ServiceDbNetwork serv;
    Utilizator userFrom;
    ObservableList<FriendRequest> reqList = FXCollections.observableArrayList();


    @FXML
    TableView<FriendRequest> requests;
    @FXML
    TableColumn<FriendRequest,String> to;
    @FXML
    TableColumn<FriendRequest,String> status;
    @FXML
    TableColumn<FriendRequest, LocalDateTime> dataR;
    @FXML
    Label userLbl;
    void setService(ServiceDbNetwork serv , Utilizator userFrom){
        this.serv=serv;
        this.userFrom=userFrom;
        userLbl.setText(userFrom.toString());
        initModel();
    }

    private void initModel() {

        List<FriendRequest> list = serv.sentRequests(userFrom.getId());
        System.out.println(list);
        reqList.setAll(list);
        to.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getId().getRight()).toString()));
        status.setCellValueFactory(new PropertyValueFactory<>("Status"));
        dataR.setCellValueFactory(new PropertyValueFactory<>("Data"));
        requests.setItems(reqList);
    }

    public void handleRmReq(ActionEvent actionEvent) {
        FriendRequest removedRequest = requests.getSelectionModel().getSelectedItem();
        if(removedRequest!=null){
            serv.removeRequest(removedRequest);

        }
        else{
            MessageAlert.showErrorMessage(null,"You did not select any request to remove!!");
        }
    }
}
