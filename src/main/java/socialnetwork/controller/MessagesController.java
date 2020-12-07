package socialnetwork.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.domain.Message;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MessagesController {
    ServiceDbNetwork serv;
    Utilizator mainUser;
    Stage mainStage;
    Stage dialogStage;
    ObservableList<Message> msgList = FXCollections.observableArrayList();
    @FXML
    Label lbl;
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


    public void setService(ServiceDbNetwork serv, Stage stage, Utilizator user) {
        this.serv=serv; mainUser=user; mainStage=stage;
        lbl.setText("HELLO "+ mainUser.toString()+" !");
    }

    public void handleSent(ActionEvent actionEvent) {
        List<Message> msgs = serv.inboxUserSENT(mainUser.getId());
        msgList.setAll(msgs);
        from.setText("TO");
        from.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getUserTo()).toString()));

        date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        subj.setCellValueFactory(new PropertyValueFactory<>("Mesaj"));
        table.setItems(msgList);
    }

    public void handleReceived(ActionEvent actionEvent) {
        List<Message> msgs = serv.inboxUser1(mainUser.getId());
        msgList.setAll(msgs);
        from.setText("FROM");
        from.setCellValueFactory(c->new SimpleStringProperty(serv.getUser(c.getValue().getUserFrom()).toString()));

        date.setCellValueFactory(new PropertyValueFactory<>("Date"));
        subj.setCellValueFactory(new PropertyValueFactory<>("Mesaj"));
        table.setItems(msgList);
    }

    public void handleNewMsg(ActionEvent actionEvent) {

        try {

            final Utilizator[] to = new Utilizator[1];
            serv.getAllUsers().forEach(x -> {
                if (x.toString().equals(toField.getText()))
                    to[0] = x;
            });
            if (to[0] == null)
                MessageAlert.showErrorMessage(null, "This user does not exist!!");
            else {
                Long reply = 0L;
                if(table.getSelectionModel().getSelectedItem()!= null)
                    reply = table.getSelectionModel().getSelectedItem().getId();
                Message m = new Message(mainUser.getId(), to[0].getId(), reply, text.getText());
                m.setId(0L);
                serv.sentNewMessage(m);
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION,"INFORMATION","message was sent succesfully!");
            }
        }catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }


    }

    public void handleShowConv(MouseEvent mouseEvent) {
        Message msg = table.getSelectionModel().getSelectedItem();
        if(msg!=null){


            AnchorPane root = new AnchorPane();
            root.setPrefHeight(300);
            root.setPrefWidth(400);
            root.setStyle("-fx-background-color: #404041");
            TextArea txt = new TextArea(msg.getMesaj());
            txt.setEditable(false);
            Label l= new Label();
            Button btn = new Button("REPLY");
            btn.setOnAction(this::handleReply);
            VBox box = new VBox(new Label(),txt,l,btn);
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
        toField.setText(serv.getUser(table.getSelectionModel().getSelectedItem().getUserFrom()).toString());
        dialogStage.close();
    }
}
