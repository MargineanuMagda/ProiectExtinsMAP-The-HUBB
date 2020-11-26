package socialnetwork;



public class Main {
    public static void main(String[] args) {
//        String fileName = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
//        String fileName2 = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendship");
//
//
//        System.out.println("Reading data from database");
//        final String url = "jdbc:postgresql://localhost:5432/retea";
//        final String username = "postgres";
//        final String pasword = "postgres";
//        Repository<Long, Utilizator> userDbRepo = new UtilizatorDb(url, username, pasword, new UtilizatorValidator());
//        FriendshipDb friendshipDbRepo = new FriendshipDb(url, username, pasword, new FrienshipValidator());
//
//        Repository<Tuple<Long, Long>, FriendRequest> repoRequest = new RequestDb(url, username, pasword, new RequestValidator());
//        Repository<Long, Message> repoMessage = new MessageDb(url,username,pasword,new MessageValidator());
//        ServiceDbNetwork serv = new ServiceDbNetwork(userDbRepo, friendshipDbRepo, repoRequest, repoMessage);
//        Consola c = new Consola(serv);
//        c.run();

        MainFX.main(args);


    }
}


