package socialnetwork;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.UserController;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.FrienshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.RequestValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDb;
import socialnetwork.repository.database.MessageDb;
import socialnetwork.repository.database.RequestDb;
import socialnetwork.repository.database.UtilizatorDb;
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
        serv = new ServiceDbNetwork(userDbRepo, friendshipDbRepo, repoRequest, repoMessage);

        initView(primaryStage);
        primaryStage.setWidth(800);
        primaryStage.setTitle("Social Network");
        primaryStage.setOpacity(0.99);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
    private void initView(Stage primaryStage) throws IOException {

        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("/views/menuView.fxml"));
        AnchorPane layout = userLoader.load();
        primaryStage.setScene(new Scene(layout));

        UserController userController = userLoader.getController();
        userController.setService(serv);

    }
}
