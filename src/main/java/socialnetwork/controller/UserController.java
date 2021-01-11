package socialnetwork.controller;


import com.itextpdf.text.DocumentException;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Event;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;
import socialnetwork.service.ServiceException;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
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

    /*@FXML
    TableView<FriendRequest> requests;
    @FXML
    TableColumn<FriendRequest, String> nameR;
    @FXML
    TableColumn<FriendRequest, String> statusR;
    @FXML
    TableColumn<FriendRequest, LocalDateTime> dataR;*/
    @FXML
    ListView<FriendRequest> listRecvReq;

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
        lbl.setText(mainUser.toString()+" !");
        List<Utilizator> users = new ArrayList<>();
        serv.getAllUsers().forEach(users::add);
        userList.setAll(users);
        toCombobox.setItems(userList);
        usetToList= new ArrayList<>();
        lbl3.setText(mainUser.toString());
        ObservableList<Utilizator> friends = FXCollections.observableArrayList();
        friends.setAll(new ArrayList<>(serv.getUserFriends1(mainUser.getId())));
        comboF.setItems(friends);

        //init sent requests
        userLbl.setText(mainUser.toString());
        initModelSENTREQ();

        //init events
        initiateEvents();
        initiateMyEvents();
        initEventLabels();
        myEvents.forEach(x->{
            //doar daca evenimentele nu s au terminat vor exista remindere
            if(x.getDate().isAfter(LocalDateTime.now())) {
                EventReminder reminder = new EventReminder(x);
                Timer t = new Timer(true);
                t.schedule(reminder, new GregorianCalendar(x.getData().getYear(), x.getData().getMonthValue() - 1, x.getData().getDayOfMonth(), x.getTime().getHour() - 1, x.getTime().getMinute()).getTime());
                myReminders.putIfAbsent(reminder, t);
            }
        });

        //init bio
        initiateBio();


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


    private void initiateRequests(int currentPage) {
        Utilizator user = mainUser;
        pagReq.setPageCount(serv.userRequest(user.getId()).size()/nrOnPages.getValue()+1);
        Iterable<FriendRequest> req = serv.getNextRequestOnPage(currentPage,user.getId());
        List<FriendRequest> reqList = StreamSupport.stream(req.spliterator(),false).collect(Collectors.toList());
        modelRequests.setAll(reqList);

        /*nameR.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getId().getLeft()).toString()));
        //nameR.setCellValueFactory(new PropertyValueFactory<>("Id"));
        statusR.setCellValueFactory(new PropertyValueFactory<>("Status"));
        dataR.setCellValueFactory(new PropertyValueFactory<>("Data"));
        requests.setItems(modelRequests);*/
        listRecvReq.setItems(modelRequests);
        listRecvReq.setCellFactory(param -> new ListCell<FriendRequest>(){
            @Override
            protected void updateItem(FriendRequest item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null || item.getId() == null)
                    setText(null);
                else
                    setText("From: "+serv.getUser(item.getId().getLeft()).toString()+ "\n on: "+item.getData());
            }
        });

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
                .filter(x-> !x.getId().equals(user.getId()) && !serv.findFriendship(x.getId(), user.getId()))
                .collect(Collectors.toList());
        //System.out.println(notFriends);
        modelNotFriends.setAll(notFriends);
        newFriend.setItems(modelNotFriends);


    }

    public void handleRequest() {
        if (listRecvReq.getSelectionModel().getSelectedItem()!=null){
            try {

                FXMLLoader loader = new FXMLLoader();
                FriendRequest friendRequest = listRecvReq.getSelectionModel().getSelectedItem();
                loader.setLocation(getClass().getResource("/views/editRequests.fxml"));
                AnchorPane root = loader.load();

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





    public void handleRmFriend() {
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

    public void handeSendRequest() {
        Utilizator userFrom = mainUser;
        Utilizator userTo = newFriend.getValue();

        if(userTo != null ){
            try{
                serv.addRequest(userFrom.getId(),userTo.getId());
                initModelSENTREQ();
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFORMATION","Request was sent succesfully!");

            }catch (ServiceException e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
               }
        else{
            MessageAlert.showErrorMessage(null,"Select a new friend first!!");
        }
        //else print a error message box
    }



    @Override
    public void update(UserChangeEvent userChangeEvent) {
        //initiate();
       // initModel();
        initiateFriends(0);
        initiateRequests(0);
        initiateMyEvents();
        initiateEvents();
    }





    public void handleLogOut() {

        Alert message=new Alert(Alert.AlertType.CONFIRMATION);

        message.getDialogPane().getStylesheets().add(MessageAlert.class.getResource("/css/style.css").toExternalForm());

        message.setHeaderText("LOG OUT");
        message.setContentText("Are you sure you want to leave");
        message.initOwner(null);
        Optional<ButtonType> result =message.showAndWait();
        if(!result.isPresent())
        // alert is exited, no button has been pressed.
        {


        }
        else
            if(result.get() == ButtonType.OK)
            {
                mainStage.close();
                message.close();

            }
            else if(result.get() == ButtonType.CANCEL){
                message.close();
            }
        // cancel button is pressed
    }

    public void handlePages() {
        Integer number = nrOnPages.getValue();

        serv.setPageSize(number);
        initiateFriends(0);
        initiateRequests(0);
    }

    public void handleChangePage() {
        System.out.println("AM SCHIMBAT PAGINA");
        int paginaCurenta = pagFriends.getCurrentPageIndex();

        initiateFriends(paginaCurenta);
    }

    public void handleChangePage1() {
        int paginaCurenta = pagReq.getCurrentPageIndex();
        initiateRequests(paginaCurenta);
    }



    //messages tab
    Stage dialogStage;
    ObservableList<Message> msgList = FXCollections.observableArrayList();
    ObservableList<Utilizator> userList = FXCollections.observableArrayList();
    List<Long> usetToList;

    @FXML
    TableView<Message> table;
    @FXML
    TableColumn<Message,String> from;
    @FXML
    TableColumn<Message, LocalDateTime> date;
    @FXML
    TableColumn<Message,String>subj;
    @FXML
    TextField toField;
    @FXML
    TextArea text;
    @FXML
    ComboBox<Utilizator> toCombobox;






    public void handleSent() {
        List<Message> msgs = serv.inboxUserSENT(mainUser.getId());
        msgList.setAll(msgs);
        from.setText("TO");
        //from.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getUserTo().get(0)).toString()));
        from.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getUserTo().stream().map(x->serv.getUser(x).toString()).reduce("",(x,y)->x+"\n"+y)));

        date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        subj.setCellValueFactory(new PropertyValueFactory<>("Mesaj"));
        table.setItems(msgList);
    }

    public void handleReceived() {
        List<Message> msgs = serv.inboxUser1(mainUser.getId());
        msgList.setAll(msgs);
        from.setText("FROM");
        from.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getUserFrom()).toString()+";"+c.getValue().getUserTo().stream()
                .filter(x-> (!x.equals(mainUser.getId())))
                .map(x->serv.getUser(x).toString())
                .reduce("",(x,y)->x+";"+y)));

        date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        subj.setCellValueFactory(new PropertyValueFactory<>("Mesaj"));
        table.setItems(msgList);
    }

    public void handleNewMsg() {

        try {


            if (usetToList.isEmpty())
                MessageAlert.showErrorMessage(null, "This user does not exist!!");
            else {
                Long reply = 0L;
                if(table.getSelectionModel().getSelectedItem()!= null)
                    reply = table.getSelectionModel().getSelectedItem().getId();
                Message m = new Message(mainUser.getId(), usetToList, reply, text.getText());
                m.setId(0L);
                serv.sentNewMessage(m);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFORMATION","message was sent succesfully!");
            }
        }catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }


    }

    public void handleShowConv() {
        Message msg = table.getSelectionModel().getSelectedItem();
        if(msg!=null){


            AnchorPane root = new AnchorPane();
            root.setPrefHeight(300);
            root.setPrefWidth(400);
            root.setStyle("-fx-background-color: #404041");
            Label from = new Label("FROM:\t"+ serv.getUser(msg.getUserFrom()));
            from.setStyle("-fx-text-fill: white");

            Label to = new Label("TO:\t"+ msg.getUserTo().stream().map(x->serv.getUser(x)).reduce("",(x,y)->x+";"+y));
            to.setStyle("-fx-text-fill: white");
            TextArea txt = new TextArea(msg.getMesaj());
            txt.setEditable(false);
            Label l= new Label();
            Button btn = new Button("REPLY");
            btn.setOnAction(this::handleReply);
            VBox box = new VBox(new Label(),from,to,txt,l,btn);
            box.setAlignment(Pos.CENTER);
            root.getChildren().addAll(box);
            //Stage
            dialogStage = new Stage();
            dialogStage.setTitle("Message");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            dialogStage.show();

        }else
            MessageAlert.showErrorMessage(null,"Please select a message first!");

    }

    private void handleReply(ActionEvent actionEvent) {
        toCombobox.setValue((Utilizator) serv.getUser(table.getSelectionModel().getSelectedItem().getUserFrom()));
        usetToList.clear();
        usetToList.add(table.getSelectionModel().getSelectedItem().getUserFrom());
        table.getSelectionModel().getSelectedItem().getUserTo().stream().filter(x-> !x.equals(mainUser.getId())).forEach(x->usetToList.add(x));
        toField.setText(serv.getUser(table.getSelectionModel().getSelectedItem().getUserFrom()).toString());
        dialogStage.close();
    }

    public void handleNewUserTo() {
        usetToList.add(toCombobox.getValue().getId());
        String s = toField.getText();
        toField.setText(s+"   "+toCombobox.getValue());
    }

    public void handleRemove() {
        toField.clear();
        usetToList.clear();
    }


    //raports controller

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



    public void handleGenerateRap1() {
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

    public void handleGenerateRap2() {
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

    //sent requests controller

    ObservableList<FriendRequest> reqList = FXCollections.observableArrayList();



    @FXML
    Label userLbl;
    @FXML
    ListView<FriendRequest> listReq;


    private void initModelSENTREQ() {

        List<FriendRequest> list = serv.sentRequests(mainUser.getId());
        System.out.println(list);
        reqList.setAll(list);
       /* to.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getId().getRight()).toString()));
        status.setCellValueFactory(new PropertyValueFactory<>("Status"));
        dataR1.setCellValueFactory(new PropertyValueFactory<>("Data"));
        requestsSENT.setItems(reqList);*/
       listReq.setItems(reqList);
        listReq.setCellFactory(param -> new ListCell<FriendRequest>(){
            @Override
            protected void updateItem(FriendRequest item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || item == null || item.getId() == null)
                    setText(null);
                else
                    setText("TO: "+serv.getUser(item.getId().getRight()).toString()+ "\n on: "+item.getData());
            }
        });

    }

    public void handleRmReq() {
        FriendRequest removedRequest = listReq.getSelectionModel().getSelectedItem();
        if(removedRequest!=null){
            serv.removeRequest(removedRequest);
            initModelSENTREQ();

        }
        else{
            MessageAlert.showErrorMessage(null,"You did not select any request to remove!!");
        }
    }
    //EVENTS

    @FXML
    TextField evName;
    @FXML
    DatePicker evDate;
    @FXML
    Slider evHour;
    @FXML
    Slider evMin;

    @FXML
    ComboBox<Event> eventCombo;

    @FXML
     TableView<Event> tableEvents;
    @FXML
            TableColumn<String,Event> nameEvent;
    @FXML
            TableColumn<LocalDate,Event> dataEvent;
    @FXML
            TableColumn<LocalTime,Event> timeEvent;


    ObservableList<Event> evList = FXCollections.observableArrayList();
    ObservableList<Event> myEvents = FXCollections.observableArrayList();
    Map<EventReminder,Timer> myReminders = new HashMap<>();

    public void initiateEvents(){

        //initiate combobox
        Iterable<Event> events = serv.getAllEvents();
        List<Event> eventsList = StreamSupport.stream(events.spliterator(),false).collect(Collectors.toList());
        evList.setAll(eventsList);
        eventCombo.setItems(evList);
    }

    public void initiateMyEvents(){
        //initiate tableView
        myEvents.setAll(serv.getUsersEvents(mainUser.getId()));
        nameEvent.setCellValueFactory(new PropertyValueFactory<>("Name"));
        dataEvent.setCellValueFactory(new PropertyValueFactory<>("Data"));
        timeEvent.setCellValueFactory(new PropertyValueFactory<>("Time"));
        tableEvents.setItems(myEvents);




    }
    @FXML
    Label lHour;
    @FXML
    Label lMin;

    public void initEventLabels() {

        evHour.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                lHour.textProperty().setValue(
                        String.valueOf(newValue.intValue()));
            }
        });
        evMin.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                lMin.textProperty().setValue(
                        String.valueOf(newValue.intValue()));
            }
        });

    }

    public void handleCreateEvent() {
        String name = evName.getText();
        LocalDateTime data = LocalDateTime.of(evDate.getValue(), LocalTime.of((int)evHour.getValue(),(int)evMin.getValue()));

        Event newEvent = new Event(name,data);
        newEvent.addParticipant(mainUser.getId());
        newEvent.setId(name);
       EventReminder reminder = new EventReminder(newEvent);
       Timer t = new Timer(true);
       t.schedule(reminder,new GregorianCalendar(newEvent.getData().getYear(),newEvent.getData().getMonthValue()-1,newEvent.getData().getDayOfMonth(),newEvent.getTime().getHour()-1,newEvent.getTime().getMinute()).getTime());
       myReminders.putIfAbsent(reminder,t);
        try{
            serv.createEvent(newEvent);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFO","Event created succesfully!");

        }
        catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }


    }

    public void handleSubscribeEvent() {
        Event event = eventCombo.getValue();
        if ( event != null) {
            if(event.getParticipants().contains(mainUser.getId()))
                MessageAlert.showErrorMessage(null,"You are already subscribed to this event!");
            else
            {
                if(event.getDate().isBefore(LocalDateTime.now())){
                    MessageAlert.showErrorMessage(null,"This event is over.Please choose another!");
                }
                else {
                    try{
                        event.addParticipant(mainUser.getId());
                        serv.updateEvent(event);
                        EventReminder reminder = new EventReminder(event);
                        Timer t = new Timer(true);
                        t.schedule(reminder,new GregorianCalendar(event.getData().getYear(),event.getData().getMonthValue()-1,event.getData().getDayOfMonth(),event.getTime().getHour()-1,event.getTime().getMinute()).getTime());
                        myReminders.putIfAbsent(reminder,t);
                        MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"INFO","You subscribed to this event succesfully!");

                    }
                    catch (Exception e){
                        MessageAlert.showErrorMessage(null,e.getMessage());
                    }
                }

            }

        }else{
            MessageAlert.showErrorMessage(null,"Please select an event to subscribe first!");
        }
    }

    public void handleUnsubscribeEvent() {
        Event eventToUnsubscribe = tableEvents.getSelectionModel().getSelectedItem();
        if(eventToUnsubscribe != null){
            try{
                eventToUnsubscribe.removeParticipant(mainUser.getId());
                serv.updateEvent(eventToUnsubscribe);
                final EventReminder[] key = new EventReminder[1];
                myReminders.forEach((x,y)->{
                    if(x.event.getName().equals( eventToUnsubscribe.getName()))
                    {
                        y.cancel();
                        key[0] =x;
                    }
                });
                myReminders.remove(key[0]);
                MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"INFO","You unsubscribed to this event succesfully!");

            }
            catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }else{
            MessageAlert.showErrorMessage(null,"Please select an event to unsubscribe first!");
        }
    }

    //------------BIO------------------
    private void initiateBio() {
        school.setWrapText(true);
        school.setText(mainUser.getSchool());
        about.setText(mainUser.getAbout());
        gender.setText(mainUser.getSex());
        living.setText(mainUser.getLiving());
        Textfrom.setText(mainUser.getFrom());
        hobby.setText(mainUser.getHobby());
    }

    @FXML
    TextArea school;
    @FXML
    Label lSchool;

    public void handleEditSchool() {
        if(school.isDisabled()){
            school.setDisable(false);
            lSchool.setText("save");
        }
        else {
            school.setDisable(true);
            lSchool.setText("edit");
        }

    }

    @FXML
    TextField living;
    @FXML
    Label lLiv;
    public void handleEditLiv() {
        if(living.isDisabled()){
            living.setDisable(false);
            lLiv.setText("save");
        }
        else {
            living.setDisable(true);
            lLiv.setText("edit");
        }
    }@FXML
    TextArea about;
    @FXML
    Label lAbout;

    public void handleEditBio() {
        if(about.isDisabled()){
            about.setDisable(false);
            lAbout.setText("save");
        }
        else {
            about.setDisable(true);
            lAbout.setText("edit");
        }
    }

    @FXML
    TextField Textfrom;
    @FXML
    Label lFrom;

    public void handleEditFrom() {
        if(Textfrom.isDisabled()){
            Textfrom.setDisable(false);
            lFrom.setText("save");
        }
        else {
            Textfrom.setDisable(true);
            lFrom.setText("edit");
        }
    }

    @FXML
    TextField gender;
    @FXML
    TextField hobby;
    @FXML
    Label lGender;
    @FXML
    Label lhobby;

    public void handleEditGender() {
        if(gender.isDisabled()){
            gender.setDisable(false);
            lGender.setText("save");
        }
        else {
            gender.setDisable(true);
            lGender.setText("edit");
        }
    }

    public void handleEditHobby() {
        if(hobby.isDisabled()){
            hobby.setDisable(false);
            lhobby.setText("save");
        }
        else {
            hobby.setDisable(true);
            lhobby.setText("edit");
        }
    }





}


class EventReminder extends TimerTask{

    Event event;
    AudioClip notifSound = new AudioClip("file:D:/Facultate/AN2SEM1/MAP/laborator/socialNetworkGUI/src/main/resources/sounds/notifEvents.mp3");


    public EventReminder(Event event) {
        this.event = event;
    }

    @Override
    public void run() {

        Platform.runLater(() -> {

            notifSound.play();

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"REMINDER","You have an hour until event: " + event);

        });
         }
}

