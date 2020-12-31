package socialnetwork;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.LogController;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.service.ServiceDbNetwork;


import java.io.IOException;

public class MainFX extends Application {

    ServiceDbNetwork serv;

    @Override
    public void start(Stage primaryStage) throws Exception {


        String fileName = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileName2 = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendship");


        System.out.println("Reading data from database");
        final String url = "jdbc:postgresql://localhost:5432/retea";
        final String username = "postgres";
        final String pasword = "postgres";
        Repository<Long, Utilizator> userDbRepo = new UtilizatorDb(url, username, pasword, new UtilizatorValidator());
        FriendshipDb friendshipDbRepo = new FriendshipDb(url, username, pasword, new FrienshipValidator());

        Repository<Tuple<Long, Long>, FriendRequest> repoRequest = new RequestDb(url, username, pasword, new RequestValidator());
        Repository<Long, Message> repoMessage = new MessageDb(url,username,pasword,new MessageValidator());
        Repository<Tuple<String,String>, LogIn> repoLogin = new LogInDb(url,username,pasword,new LogInValidator());
        Repository<String,Event> repoEvents = new EventDb(url,username,pasword,new EventValidator());
        serv = new ServiceDbNetwork(userDbRepo, friendshipDbRepo, repoRequest, repoMessage, repoLogin, repoEvents);

        initView(primaryStage);
        //primaryStage.setWidth(800);
        primaryStage.setTitle("Social Network");
        primaryStage.setOpacity(0.99);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("/views/logView.fxml"));
        AnchorPane layout = userLoader.load();
        primaryStage.setScene(new Scene(layout));

        //UserController userController = userLoader.getController();
        //userController.setService(serv);
        LogController logController = userLoader.getController();
        logController.setService(serv,primaryStage);

    }
}
