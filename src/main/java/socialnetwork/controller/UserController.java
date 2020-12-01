package socialnetwork.controller;


import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {

    ServiceDbNetwork serv;
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelFriends = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelRequests = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelNotFriends=FXCollections.observableArrayList();


    @FXML
    ComboBox<Utilizator> logIn;

    @FXML
    ComboBox<Utilizator> newFriend;

    @FXML
    TableView<Utilizator> friends;

    @FXML
    TableColumn<Utilizator, String> lastname;
    @FXML
    TableColumn<Utilizator,String> firstname;

    @FXML
    TableView<FriendRequest> requests;
    @FXML
    TableColumn<FriendRequest, String> nameR;
    @FXML
    TableColumn<FriendRequest, String> statusR;
    @FXML
    TableColumn<FriendRequest, LocalDateTime> dataR;

    @FXML
    Button addFBtn;

    @FXML
    Button rmFBtn;

    public void setService( ServiceDbNetwork serv){

        this.serv = serv;
        serv.addObserver(this);
        initModel();
        initiate();
    }

    private void initModel() {
        Iterable<Utilizator> users = serv.getAllUsers();
        List<Utilizator> usersList = StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
        modelUsers.setAll(usersList);

    }

    private void initiate() {
        logIn.setItems(modelUsers);

        //logIn.setOnAction(event);
    }

    @FXML
    public void handleLogIn(ActionEvent actionEvent) {
        initiateFriends();
        initiateRequests();

    }



    private void initiateRequests() {
        Utilizator user = logIn.getValue();
        Iterable<FriendRequest> req = serv.userRequest(user.getId());
        List<FriendRequest> reqList = StreamSupport.stream(req.spliterator(),false).collect(Collectors.toList());
        modelRequests.setAll(reqList);

        nameR.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getId().getLeft()).toString()));

        statusR.setCellValueFactory(new PropertyValueFactory<>("Status"));
        dataR.setCellValueFactory(new PropertyValueFactory<>("Data"));
        requests.setItems(modelRequests);
    }




    private void initiateFriends() {
        Utilizator user = logIn.getValue();
        Iterable<Utilizator> users = serv.getUserFriends1(user.getId());
        List<Utilizator> usersList = StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
        modelFriends.setAll(usersList);
        firstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friends.setItems(modelFriends);

        List<Utilizator> userList1= StreamSupport.stream(serv.getAllUsers().spliterator(),false).collect(Collectors.toList());
        List<Utilizator> notFriends=userList1.stream()
                .filter(x->{
                    if(x.getId()!=user.getId() && serv.findFriendship(x.getId(),user.getId()) == false)
                        return true;
                    else return false;
                })
                .collect(Collectors.toList());
        //System.out.println(notFriends);
        modelNotFriends.setAll(notFriends);
        newFriend.setItems(modelNotFriends);


    }

    public void handleRequest(MouseEvent mouseEvent) {
        if (requests.getSelectionModel().getSelectedItem()!=null){
            try {

                FXMLLoader loader = new FXMLLoader();
                FriendRequest friendRequest = requests.getSelectionModel().getSelectedItem();
                loader.setLocation(getClass().getResource("/views/editRequests.fxml"));
                AnchorPane root = (AnchorPane) loader.load();

                //Stage
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Edit requests");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                RequestController reqCtrl = loader.getController();
                reqCtrl.setService(serv,dialogStage,friendRequest);
                dialogStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
            MessageAlert.showErrorMessage(null,"Select the request you want to edit first!!");


    }



    public void handleFilteredSearch(ActionEvent actionEvent) {
        String text = newFriend.getEditor().getText();

        ObservableList<Utilizator> modelNF=FXCollections.observableArrayList();
        modelNF.setAll(modelNotFriends.stream().filter(x->x.getFirstName().matches(".*"+text+".*")||x.getLastName().matches(".*"+text+".*"))
                .collect(Collectors.toList()));
        newFriend.setItems(modelNF);


        System.out.println(newFriend.getValue());
    }

    public void handleRmFriend(ActionEvent actionEvent) {
        Utilizator rmFriend =friends.getSelectionModel().getSelectedItem();
        Utilizator user = logIn.getValue();
        if(rmFriend!=null){
            try {
                serv.removeFriend(user.getId(), rmFriend.getId());
                MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"INFORMATION","Friend removed succesfully!!");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else
            MessageAlert.showErrorMessage(null,"Select the friend you want to remove first!!");
        //else print a error message box
    }

    public void handeSendRequest(ActionEvent actionEvent) {
        Utilizator userFrom = logIn.getValue();
        Utilizator userTo = newFriend.getValue();

        if(userTo != null){
            serv.addRequest(userFrom.getId(),userTo.getId());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFORMATION","Request was sent succesfully!");
        }
        else{
            MessageAlert.showErrorMessage(null,"Select a new friend first!!");
        }
        //else print a error message box
    }

    public void handeSENTRequest(ActionEvent actionEvent) {
        if(logIn.getValue()!=null){
            try {

                FXMLLoader loader = new FXMLLoader();
                Utilizator user = logIn.getValue();
                loader.setLocation(getClass().getResource("/views/sentRequestsView.fxml"));
                AnchorPane root = (AnchorPane) loader.load();

                //Stage
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Sent requests");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                SentRequests reqCtrl = loader.getController();
                reqCtrl.setService(serv,user);
                dialogStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            MessageAlert.showErrorMessage(null,"You are not logged in!");

    }

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        //initiate();
       // initModel();
        initiateFriends();
        initiateRequests();
    }
}

