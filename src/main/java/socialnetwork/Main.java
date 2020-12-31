package socialnetwork;


import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.database.*;
import socialnetwork.repository.paging.PagingRepository;
import socialnetwork.service.ServiceDbNetwork;
import socialnetwork.utils.events.EventReminder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
       /* String fileName = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String fileName2 = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendship");


        System.out.println("Reading data from database");
        final String url = "jdbc:postgresql://localhost:5432/retea";
        final String username = "postgres";
        final String pasword = "postgres";
        PagingRepository<Long, Utilizator> userDbRepo = new UtilizatorDb(url, username, pasword, new UtilizatorValidator());
        FriendshipDb friendshipDbRepo = new FriendshipDb(url, username, pasword, new FrienshipValidator());

        PagingRepository<Tuple<Long, Long>, FriendRequest> repoRequest = new RequestDb(url, username, pasword, new RequestValidator());
        PagingRepository<Long, Message> repoMessage = new MessageDb(url,username,pasword,new MessageValidator());
        PagingRepository<Tuple<String,String>, LogIn> repoLogin = new LogInDb(url,username,pasword,new LogInValidator());

        ServiceDbNetwork serv = new ServiceDbNetwork(userDbRepo, friendshipDbRepo, repoRequest, repoMessage, repoLogin, repoEvents);

        serv.setPageSize(3);
        serv.getNextFriendshipsOnPage(0).forEach(x-> System.out.println(x));
*/
        //serv.sentNewMessage(m);
//        Consola c = new Consola(serv);
//        c.run();

         /*LogIn log = new LogIn(0L);
        log.setId(new Tuple<>("larisa@stud.ubbcluj.ro","1234567"));
        repoLogin.save(log);
        System.out.println(repoLogin.findOne(new Tuple<>("magdalena@stud.ubbcluj.ro","1234567")));
       */

       // System.out.println(serv.sentRequests(3L));
        EventReminder ev1 = new EventReminder("Examen", LocalDateTime.of(LocalDate.now(), LocalTime.parse("22:45:00")));
        Timer timer = new Timer(true);

        timer.schedule(ev1,new GregorianCalendar(2020, 11, 30,22,31).getTime());

        MainFX.main(args);

    }
}


