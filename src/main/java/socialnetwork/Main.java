package socialnetwork;


import socialnetwork.config.ApplicationContext;
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

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
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
        ServiceDbNetwork serv = new ServiceDbNetwork(userDbRepo, friendshipDbRepo, repoRequest, repoMessage);
        Message m = new Message(12L, Arrays.asList(3L,3L,9L),0L,"VVVV");
        m.setId(0L);

        //serv.sentNewMessage(m);
//        Consola c = new Consola(serv);
//        c.run();


       MainFX.main(args);
       // System.out.println(serv.sentRequests(3L));


    }
}


