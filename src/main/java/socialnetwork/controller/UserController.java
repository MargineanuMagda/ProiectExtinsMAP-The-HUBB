package socialnetwork.controller;


import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController {

    ServiceDbNetwork serv;
    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelFriends = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelRequests = FXCollections.observableArrayList();

    @FXML
    ComboBox<Utilizator> logIn;


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

    public void setService( ServiceDbNetwork serv){

        this.serv = serv;
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


    }
}

