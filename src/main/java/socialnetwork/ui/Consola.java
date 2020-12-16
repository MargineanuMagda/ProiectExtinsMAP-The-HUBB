package socialnetwork.ui;


import com.itextpdf.text.DocumentException;
import socialnetwork.domain.FriendRequest;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.ServiceDbNetwork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User interface
 */

public class Consola {
    private final ServiceDbNetwork serv;
    private final InputStreamReader isr = new InputStreamReader(System.in);
    private final BufferedReader br = new BufferedReader(isr);

    public Consola(ServiceDbNetwork serv) {
        this.serv = serv;
    }

/**
     * add a new user
     * @throws IOException
     */

    public void addUser() throws IOException {

        System.out.println("Introduceti prenumele userului: ");
        String prenume = br.readLine();

        System.out.println("Introduceti numele userului: ");
        String nume = br.readLine();

        System.out.println("Introduceti id-ul userului: ");
        Long l = Long.parseLong(br.readLine());

        System.out.println("Nume: " + nume + " Prenume: " + prenume + "\n");
        Utilizator user = new Utilizator(prenume, nume);
        user.setId(l);
        serv.addUtilizator(user);


    }

/**
     * remove a user
     * @throws IOException
     */

    public void removeUser() throws IOException {


        System.out.println("Introduceti id-ul userului pe care doriti a-l sterge: ");
        Long l = Long.parseLong(br.readLine());

        serv.deleteUtilizator(l);
    }

/**
     * add a friendship
     * @throws IOException
     */

    public void addFriend() throws IOException {

        System.out.println("Introduceti id-ul primului prieten: ");
        Long l1 = Long.parseLong(br.readLine());
        System.out.println("Introduceti id-ul celui de-al 2-lea prieten: ");
        Long l2 = Long.parseLong(br.readLine());

        serv.addFriend(l1, l2);

    }

/**
     * remove a friendship
     * @throws IOException
     */

    public void removeFriend() throws IOException {

        System.out.println("Introduceti id-ul primului prieten pe care doriti a-l sterge: ");
        Long l1 = Long.parseLong(br.readLine());
        System.out.println("Introduceti id-ul celui de-al 2-lea prieten pe care doriti a-l sterge: ");
        Long l2 = Long.parseLong(br.readLine());

        serv.removeFriend(l1, l2);
    }

/**
     * print all users
     */

    public void printUsers() {
        serv.getAllUsers().forEach(System.out::println);
    }

/**
     * print all friendships
     */

    public void printFrienships() {
        serv.getAllPrietenii().forEach(System.out::println);
    }

/**
     * print the number of communities
     */

    public void nrCommunities() {
        System.out.println("Numarul de communitati este: " + serv.nrCommunities());
    }

/**
     * print the list of useer
     */

    public void largestCommunity() {
        serv.longestPath().forEach(x -> System.out.println(x.getFirstName() + " " + x.getLastName() + "-->"));
    }

/**
     * print users's friends
     * @throws IOException
     */

    private void userFriends() throws IOException, DocumentException {

        System.out.println("Introduceti id-ul userului pentru care afiseaza prietenii: ");
        long l1 = Long.parseLong(br.readLine());
        List<String> raport = serv.getUserFriends(l1);
        raport.forEach(System.out::println);
        System.out.println("Doriti sa salvati raportul in format PDF?");
        String rasp = br.readLine();
        if(rasp.equals("da")){
            System.out.println("Introduceti numele fisierului\n");
            String filename = br.readLine();
            serv.saveRaportToPDF(raport,filename,"Afiseaza prietenii unui user");
        }

    }

/**
     * print users's friends filtered by month
     * @throws IOException
     */

    private void filterDate() throws IOException, DocumentException {
        System.out.println("Introduceti id-ul userului pentru care afiseaza prietenii: ");
        Long l1 = Long.parseLong(br.readLine());
        boolean ok=true;
        List<String> raport = null;
        while(ok){
            System.out.println("Introduceti luna in care au fost facute prieteniile: (1->12)");
            int month = Integer.parseInt(br.readLine());
            if(month>0 && month<=12)
            {
                ok=false;
                raport = serv.getUserFriendsByMonth(l1,month);
                raport.forEach(x-> System.out.println(x));
            }
            else
                System.out.println("Intoduceti o luna intre 1 si 12!!");
        }
        System.out.println("Doriti sa salvati raportul in format PDF?");
        String rasp = br.readLine();
        if(rasp.equals("da")){
            System.out.println("Introduceti numele fisierului\n");
            String filename = br.readLine();
            serv.saveRaportToPDF(raport,filename,"Afiseaza prietenii filtrati dupa luna a unui user");
        }

    }


    void addRequest() throws IOException {

        System.out.println("USERI INREGISTRATI\n");
        serv.getAllUsers().forEach(System.out::println);
        System.out.println("Introduceti id-ul userului ce trimite cererea de prietenie: ");
        Long l1 = Long.parseLong(br.readLine());
        System.out.println("Introduceti id-ul userului carui i se trimite cererea: ");
        Long l2 = Long.parseLong(br.readLine());

        serv.addRequest(l1,l2);
    }

    void changeStatus() throws IOException{
        System.out.println("Introduceti id-ul userului ce a primit cererea: ");
        long l1 = Long.parseLong(br.readLine());
        System.out.println("CERERI PRIMITE");
        serv.userRequest(l1)
                .forEach(x-> System.out.println("FROM: "+x.getId().getLeft()+"\tSTATUS: "+ x.getStatus()));
        System.out.println("Introduceti id-ul userului de la care a primit cererea: ");
        long l2 = Long.parseLong(br.readLine());
        System.out.println("Introduceti noul status: ");
        String status = br.readLine();

        FriendRequest request = new FriendRequest(status, LocalDate.now());
        Tuple<Long,Long> ids = new Tuple<>(l2,l1);
        request.setId(ids);
        serv.changeStatus(request);
    }
    private void sentMessage()throws IOException {
        System.out.println("USERI INREGISTRATI\n");
        serv.getAllUsers().forEach(System.out::println);
        System.out.println("Introduceti id-ul userului ce trimite mesajul ");
        Long l1 = Long.parseLong(br.readLine());
        boolean ok = true;
        List<Long> ids = new ArrayList<>();
        while (ok){
            System.out.println("Introduceti id-ul userului caruia ii trimiteti mesajul: ");
            Long l2 = Long.parseLong(br.readLine());
            ids.add(l2);
            System.out.println("Doriti sa trimiteti mesajul si altui user?");
            String rasp = br.readLine();
            if (rasp.equals("nu"))
                ok = false;
            else
                if ( !rasp.equals("da"))
                    System.out.println("Puteti raspunde cu da/nu!\n");

        }

        System.out.println("Introduceti mesajul: ");
        String mesaj = br.readLine();

        ids.forEach(x->{
            Message mes = new Message(l1, Arrays.asList(x),0L,mesaj);
            mes.setId(0L);
            serv.sentNewMessage(mes);
        });

        System.out.println("Mesaj trimis cu succes!");
    }
  /*  private void replyMessage() throws IOException{
        System.out.println("Introduceti id-ul userului ce trimite un reply ");
        Long l1 = Long.parseLong(br.readLine());
        System.out.println(serv.inboxUser(l1));
        if(serv.inboxUser(l1).size() == 0)
            throw new IOException("Userul are inboxul gol!");
        System.out.println("Introduceti id-ul conversatiei la care doriti sa dati reply");

        Long l2 = Long.parseLong(br.readLine());
        System.out.println("Introduceti mesajul: ");
        String mesaj = br.readLine();
        serv.replyMessage(l2,mesaj);
        System.out.println("Reply trimis cu succes!\n");
    }*/

    private void showConversation() throws IOException{

            System.out.println("Introduceti id-ul primului user  ");
            Long l1 = Long.parseLong(br.readLine());
            System.out.println("Introduceti id-ul celui de-al 2lea user ");
            Long l2 = Long.parseLong(br.readLine());
        System.out.println(serv.showConversation(l1,l2));

    }
    public void submenuUser()throws Exception{
        System.out.println("Meniul comenzilor este:\n0.Terminare program\n1.Adaugare utilizator\n2.Sterge utilizator\n3.Adaugare prietenie\n4.Stergere prietenie\n");

        System.out.println("Introduceti o comanda:");
        int c=Integer.parseInt(br.readLine());
        switch (c){
            case 0:
                return;
            case 1:
                addUser();
                break;
            case 2:
                removeUser();
                break;
            case 3:
                addFriend();
                break;
            case 4:
                removeFriend();
                break;
            default:
                System.out.println("Comanda invalida!\n");
                break;
        }

    }
    public void submeniuCommunities()throws Exception{
        System.out.println("SUBMENIU COMUNITATI\n\n1.Afisare numar comunitati\n2.Afisare cea mai sociabila comunitate\n");
        System.out.println("Introduceti o comanda:");
        int c=Integer.parseInt(br.readLine());
        switch (c){
            case 0:
                return;
               // break;
            case 1:
                nrCommunities();
                break;
            case 2:
                largestCommunity();
                break;
            default:
                System.out.println("Comanda invalida!\n");
                break;
        }
    }

    void submeniuFilters()throws Exception{

        System.out.println("SUBMENIU FILTRE\n\n1.Afisare prieteni user\n2.Filtrare prieteni dupa luna\n");
        System.out.println("Introduceti o comanda:");
        int c=Integer.parseInt(br.readLine());
        switch (c){
            case 0:
                return;
            case 1:
                userFriends();
                break;
            case 2:
                filterDate();
                break;

            default:
                System.out.println("Comanda invalida!\n");
                break;
        }
    }



    void submeniuMessage()throws Exception{
        System.out.println("SUBMENIU MESAJE\n\n1.Trimite mesaj nou\n2.Trimite reply\n3.Afiseaza conversatia\n");
        System.out.println("Introduceti o comanda:");
        int c=Integer.parseInt(br.readLine());
        switch (c){
            case 0:
                return;
            case 1:
                sentMessage();
                break;
            case 2:
                //replyMessage();
                break;
            case 3:
                showConversation();
                break;
            default:
                System.out.println("Comanda invalida!\n");
                break;
        }
    }
    void submeniuRequest()throws Exception{
        System.out.println("SUBMENIU FRIENDREQUEST\n\n1.Trimite cerere de prietenie\n2.Raspunde cerere de prietenie\n");
        System.out.println("Introduceti o comanda:");
        int c=Integer.parseInt(br.readLine());
        switch (c){
            case 0:
                return;
            case 1:
                addRequest();
                break;
            case 2:
                changeStatus();
                break;
            default:
                System.out.println("Comanda invalida!\n");
                break;
        }
    }

    public void run() {
         while (true) {
             System.out.println("SUBMENIURI\n1.USER\n2.COMMUNITIES\n3.FILTERS\n4.MESSAGES\n5.FRIENDREQUEST\n");
            System.out.println("Introduceti o comanda...\n");
            String s;
            int cmd = 0;
            try {
                s = br.readLine();
                cmd = Integer.parseInt(s);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {

                switch (cmd) {
                    case 0:
                        break;
                    case 1:
                        submenuUser();
                        break;
                    case 2:
                        submeniuCommunities();
                        break;
                    case 3:
                        submeniuFilters();
                        break;
                    case 4:
                        submeniuMessage();
                        break;
                    case 5:
                        submeniuRequest();
                        break;

                    case -1:
                        printUsers();
                        break;
                    case -2:
                        printFrienships();
                        break;
                    default:
                        System.out.println("Comanda invalida!");
                        break;
                }
            } catch (NumberFormatException n) {
                System.out.println("Id invalid!!!\n");
            } catch (Exception i) {
                i.printStackTrace();

            }
             if (cmd == 0)
                break;

        }
    }




}
