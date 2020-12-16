package socialnetwork.controller;


import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserController implements Observer<UserChangeEvent> {

    ServiceDbNetwork serv;

    ObservableList<Utilizator> modelUsers = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelFriends = FXCollections.observableArrayList();
    ObservableList<FriendRequest> modelRequests = FXCollections.observableArrayList();
    ObservableList<Utilizator> modelNotFriends=FXCollections.observableArrayList();

    Stage mainStage;
    Utilizator mainUser;
    @FXML
    Label lbl;

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

    @FXML
    Spinner<Integer> nrOnPages;

    @FXML
    Pagination pagFriends;
    @FXML
    Pagination pagReq;


    public void setService(ServiceDbNetwork serv, Utilizator user, Stage dialogStage){

        this.serv = serv;
        mainUser=user;
        mainStage=dialogStage;
        serv.addObserver(this);
        initModel();
        initiate();
        initiateFriends(0);
        initiateRequests(0);


    }

    private void initModel() {
        Iterable<Utilizator> users = serv.getAllUsers();
        List<Utilizator> usersList = StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
        modelUsers.setAll(usersList);

    }

    private void initiate() {
       lbl.setText(mainUser.toString());

        //logIn.setOnAction(event);
    }

    @FXML
    public void handleLogIn() {
        try {
            FXMLLoader loader = new FXMLLoader();

            loader.setLocation(getClass().getResource("/views/logView.fxml"));
            AnchorPane root =  loader.load();

            //Stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LogController logCtrl = loader.getController();
            logCtrl.setService(serv,dialogStage);
            dialogStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void initiateRequests(int currentPage) {
        Utilizator user = mainUser;
        pagReq.setPageCount(serv.userRequest(user.getId()).size()/nrOnPages.getValue()+1);
        Iterable<FriendRequest> req = serv.getNextRequestOnPage(currentPage,user.getId());
        List<FriendRequest> reqList = StreamSupport.stream(req.spliterator(),false).collect(Collectors.toList());
        modelRequests.setAll(reqList);

        nameR.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getId().getLeft()).toString()));
        //nameR.setCellValueFactory(new PropertyValueFactory<>("Id"));
        statusR.setCellValueFactory(new PropertyValueFactory<>("Status"));
        dataR.setCellValueFactory(new PropertyValueFactory<>("Data"));
        requests.setItems(modelRequests);
    }




    private void initiateFriends(int currentPage) {
        Utilizator user = mainUser;
        int pag = nrOnPages.getValue();
        serv.setPageSize(pag);

        Iterable<Utilizator> users = serv.getNextFriendOnPage(currentPage,user.getId());

        List<Utilizator> usersList = StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
        pagFriends.setPageCount(serv.getUserFriends1(user.getId()).size()/pag+1);
        modelFriends.setAll(usersList);
        firstname.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        friends.setItems(modelFriends);

        List<Utilizator> userList1= StreamSupport.stream(serv.getAllUsers().spliterator(),false).collect(Collectors.toList());
        List<Utilizator> notFriends=userList1.stream()
                .filter(x->{
                    return x.getId() != user.getId() && serv.findFriendship(x.getId(), user.getId()) == false;
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
        Utilizator user = mainUser;
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
        Utilizator userFrom = mainUser;
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

            try {

                FXMLLoader loader = new FXMLLoader();
                Utilizator user = mainUser;
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

    @Override
    public void update(UserChangeEvent userChangeEvent) {
        //initiate();
       // initModel();
        initiateFriends(0);
        initiateRequests(0);
    }

    public void handleMessages(ActionEvent actionEvent) {
        if(mainUser!=null){
            try {

                FXMLLoader loader = new FXMLLoader();
                Utilizator user = mainUser;
                loader.setLocation(getClass().getResource("/views/messagesView.fxml"));
                AnchorPane root = (AnchorPane) loader.load();

                //Stage
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Messages");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                MessagesController msgCtrl = loader.getController();
                msgCtrl.setService(serv,dialogStage,user);
                dialogStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            MessageAlert.showErrorMessage(null,"You are not logged in!");

    }

    public void handeRapoarte(ActionEvent actionEvent) {
        if(mainUser!=null){
            try {

                FXMLLoader loader = new FXMLLoader();
                Utilizator user = mainUser;
                loader.setLocation(getClass().getResource("/views/raportsView.fxml"));
                AnchorPane root = (AnchorPane) loader.load();

                //Stage
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Raports");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                RaportsController rCtrl = loader.getController();
                rCtrl.setService(serv,dialogStage,user);
                dialogStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            MessageAlert.showErrorMessage(null,"You are not logged in!");


    }

    public void handleLogOut(MouseEvent mouseEvent) {
        Stage dialogStage = new Stage();
        AnchorPane root = new AnchorPane();
        root.setPrefHeight(300);
        root.setPrefWidth(400);
        root.setStyle("-fx-background-color: #404041");
        Label info = new Label(mainUser.toString()+"\n Are you sure you want to log out?");
        info.setStyle("-fx-text-fill: white");
        info.setPrefHeight(100);

        Button btnYes = new Button("YES");
        Button btnNo = new Button("NO");
        btnYes.setOnAction(x->{mainStage.close();dialogStage.close();});
        btnNo.setOnAction(x->dialogStage.close());
        HBox hbox = new HBox(btnYes,new Label("     "),btnNo);
        VBox box = new VBox(new Label(),info,hbox);
        box.setAlignment(Pos.CENTER);
        root.getChildren().addAll(box);
        //Stage

        dialogStage.setTitle("Message");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        dialogStage.show();
    }

    public void handlePages(MouseEvent mouseEvent) {
        Integer number = nrOnPages.getValue();

        serv.setPageSize(number);
        initiateFriends(0);
        initiateRequests(0);
    }

    public void handleChangePage(MouseEvent mouseEvent) {
        System.out.println("AM SCHIMBAT PAGINA");
        int paginaCurenta = pagFriends.getCurrentPageIndex();

        initiateFriends(paginaCurenta);
    }

    public void handleChangePage1(MouseEvent mouseEvent) {
        int paginaCurenta = pagReq.getCurrentPageIndex();
        initiateRequests(paginaCurenta);
    }
}

