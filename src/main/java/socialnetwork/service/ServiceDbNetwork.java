package socialnetwork.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import socialnetwork.domain.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.RepositoryException;
import socialnetwork.repository.database.FriendshipDb;
import socialnetwork.utils.Utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceDbNetwork {

    //Service has 2 types of repo: repoUsers ans a repoFrienship
    private final Repository<Long, Utilizator> repoUsers;
    private final FriendshipDb repoFriendship;
    private final Repository<Tuple<Long,Long>, FriendRequest> repoRequest;
    private final Repository<Long,Message> repoMessage;

    //Graph structure used for finding comunities
    //dictionar ce creeaza o relatie de 1-1 intre noduri si id urile userilor
    private final Map<Long, Integer> map = new HashMap<>();
    private final Map<Integer, Long> reverseMap = new HashMap<>();
    private Boolean[][] adj;//matrice de adiacenta
    private int nodes;//numar noduri
    private Boolean[] visited;


    public ServiceDbNetwork(Repository<Long, Utilizator> repo, FriendshipDb repo1, Repository<Tuple<Long, Long>, FriendRequest> repoRequest, Repository<Long, Message> repoMessage) {
        this.repoUsers = repo;
        this.repoFriendship = repo1;
        this.repoRequest = repoRequest;
        this.repoMessage = repoMessage;
    }


    /**
     * add a new user
     *
     * @param messageTask- the user we try to add in
     * @throws ServiceException when the messageTask's ID is already in repoUser
     */
    public void addUtilizator(Utilizator messageTask) {
        Utilizator task = repoUsers.save(messageTask);
        if (task != null)
            throw new ServiceException("ID existent!\n");

    }


    /**
     * delete a user
     * -delete all the friendships which contains entity' Id from RepoFriendship
     * -the delete the user from repoUser
     *
     * @param ID-delete the user which contains id
     * @throws ServiceException when
     *                          id does not exist in repo
     */
    public void deleteUtilizator(Long ID) {
        Utilizator userRemoved = repoUsers.findOne(ID);
        if (userRemoved == null)
            throw new ServiceException("Service exception: ID invalid pentru stergere!\n");

        //List<Utilizator> friends = userRemoved.getFriends();
       /* repoFriendship.getFriends(ID).forEach(x -> {
            if (ID < x)
                repoFriendship.delete(new Tuple<>(ID, x));
            else
                repoFriendship.delete(new Tuple<>(x, ID));
        });
        */
        repoUsers.delete(ID);

    }

    /**
     * add a new firendship between 2 users
     *
     * @param ID1- id of first user
     * @param ID2- second user's id
     * @throws ServiceException when
     *                          one of the IDs does not exist in repoUsers(or both)
     *                          the IDs are equal
     *                          the friendship exists in RepoFriendship
     */
    public void addFriend(Long ID1, Long ID2) {

        Utilizator User1 = repoUsers.findOne(ID1);
        Utilizator User2 = repoUsers.findOne(ID2);
        if (User1 == null || User2 == null)
            throw new ServiceException("Adaugare esuata: Id uri inexistente!\n");

        Tuple<Long, Long> idPrietenie;
        if (ID1 < ID2)
            idPrietenie = new Tuple<>(ID1, ID2);
        else if (ID1 > ID2)
            idPrietenie = new Tuple<>(ID2, ID1);
        else
            throw new ServiceException("Adaugare prietenie invalida!Acelasi ID\n");

        Prietenie p = new Prietenie();
        p.setId(idPrietenie);
        System.out.println("Prietenie " + p.toString());
        Prietenie pnew = repoFriendship.save(p);
        if (pnew != null)
            throw new ServiceException("Prietenie existenta!!\n");


    }

    /**
     * delete a friendship
     *
     * @param ID1 - first id
     * @param ID2 - second is
     * @throws ServiceException when
     *                          one of the IDs does not exist in repoUsers(or both)
     *                          the IDs are equal
     *                          the friendship doesn t exist
     */
    public void removeFriend(Long ID1, Long ID2) {
        Utilizator User1 = repoUsers.findOne(ID1);
        Utilizator User2 = repoUsers.findOne(ID2);
        if (User1 == null || User2 == null)
            throw new ServiceException("Adaugare esuata: Id uri inexistente!\n");


        Tuple<Long, Long> idPrietenie;
        if (ID1 < ID2)
            idPrietenie = new Tuple<>(ID1, ID2);
        else if (ID1 > ID2)
            idPrietenie = new Tuple<>(ID2, ID1);
        else
            throw new ServiceException("Adaugare prietenie invalida!Acelasi ID\n");

        Prietenie p = new Prietenie();
        p.setId(idPrietenie);
        System.out.println("Prietenie " + p.toString());
        Prietenie prietenie = repoFriendship.delete(idPrietenie);
        if (prietenie == null)
            throw new ServiceException("Stergere esuata: Prietenie inexistenta!\n");


    }

    /**
     * @return list of users
     */
    public Iterable<Utilizator> getAllUsers() {
        return repoUsers.findAll();
    }

    /**
     * @return list of friendships
     */
    public Iterable<Prietenie> getAllPrietenii() {
        return repoFriendship.findAll();
    }


    /**
     * initializeaza graful prieteniilor
     * -initializeaza matricea de adiacenta
     * -vectorul vizitat
     * -creeaza o relatie 1-1 inre id-uri si noduri
     */
    private void initGraph() {
        nodes = repoUsers.size();
        adj = new Boolean[nodes][nodes];
        visited = new Boolean[nodes];
        //matricea de adiacenta
        for (int i = 0; i < nodes; i++)
            Arrays.fill(adj[i], false);
        Arrays.fill(visited, false);

        //map care are care cheie id-ul userului si ca valoare un nod intr 0...nodes-1

        final Integer[] key = {0};
        repoUsers.findAll().forEach(x -> {
            map.putIfAbsent(x.getId(), key[0]);
            reverseMap.putIfAbsent(key[0], x.getId());
            key[0]++;
        });

        repoFriendship.findAll().forEach(x -> {
            adj[map.get(x.getId().getRight())][map.get(x.getId().getLeft())] = true;
            adj[map.get(x.getId().getLeft())][map.get(x.getId().getRight())] = true;

        });
    }

    /**
     * adauga in comunity toate nodurile care fac parte dintr o componenta conexa, folosind dfs
     *
     * @param nod       - int
     * @param comunity- list of users
     */
    public void dfs(int nod, List<Integer> comunity) {
        comunity.add(nod);
        visited[nod] = true;
        for (int i = 0; i < nodes; i++) {
            if (adj[nod][i] && !visited[i])
                dfs(i, comunity);
        }
    }

    /**
     * calculate the number of comunities using DFS on a unoriented graph
     *
     * @return number of communities
     */
    public int nrCommunities() {

        initGraph();
        int n = 0;
        for (int i = 0; i < nodes; i++) {
            if (!visited[i]) {
                n++;
                dfs(i, new ArrayList<>());
            }

        }
        return n;

    }

    /**
     * find the largest community of friends
     *
     * @return list of friends
     */
    public List<Utilizator> longestPath() {
        initGraph();
        List<Integer> maxCicle = new ArrayList<>();
        int maxN = 0;
        for (int i = 0; i < nodes; i++) {
            if (!visited[i]) {
                List<Integer> conex = new ArrayList<>();
                dfs(i, conex);
                if (conex.size() > maxN)
                    maxCicle = conex;
                maxN = conex.size();

            }
        }
        List<Utilizator> comunity = new ArrayList<>();
        maxCicle.forEach(x -> comunity.add(repoUsers.findOne(reverseMap.get(x))));
        return comunity;

    }

    /**
     * Return the user's friends
     *
     * @param l1-the id of the user whose friends we are looking for
     * @throws ServiceException when
     *                          id does not exist in repo
     * @return list of strings containing firstname,lastname and date when friendship was added
     */
    public List<String> getUserFriends(long l1) {

        if(repoUsers.findOne(l1) == null)
            throw new RepositoryException("User inexistent!\n");
        return repoFriendship.getFriends(l1).stream()
                .map(repoUsers::findOne)
                .map(x -> {

                    String s = "Prenume:\t" + x.getFirstName() + "\t|Nume:\t" + x.getLastName() + "\t|Data:\t";
                    Prietenie p = repoFriendship.findOne(new Tuple<>(l1, x.getId()));
                    if (p == null)
                        s += repoFriendship.findOne(new Tuple<>(x.getId(), l1)).getDate().format(Utils.myFormatObj);
                    else
                        s += p.getDate().format(Utils.myFormatObj);

                    return s;
                })
                .collect(Collectors.toList());
    }
    public List<Utilizator> getUserFriends1(Long l1){
        if(repoUsers.findOne(l1) == null)
            throw new RepositoryException("User inexistent!\n");
        return repoFriendship.getFriends(l1).stream()
                .map(repoUsers::findOne)
                .collect(Collectors.toList());
    }

    /**
     * filter user s friends by month
     *
     * @param l1-the id of the user whose friends we are looking for
     * @param month  - month
     * @throws ServiceException when
     *                          id does not exist in repo
     * @return list of users filtered by month
     */
    public List<String> getUserFriendsByMonth(Long l1, Integer month) {
        if(repoUsers.findOne(l1) == null)
            throw new RepositoryException("User inexistent!\n");
        return repoFriendship.getFriends(l1).stream()
                .map(repoUsers::findOne)
                .filter(x -> {
                    Prietenie p = repoFriendship.findOne(new Tuple<>(l1, x.getId()));
                    if (p == null)
                        p = repoFriendship.findOne(new Tuple<>(x.getId(), l1));
                    return p.getDate().getMonthValue() == month;
                })
                .map(x -> {
                    String s = "Prenume:\t" + x.getFirstName() + "\t|Nume:\t" + x.getLastName() + "\t|Data:\t";
                    Prietenie p = repoFriendship.findOne(new Tuple<>(l1, x.getId()));
                    if (p == null)
                        s += repoFriendship.findOne(new Tuple<>(x.getId(), l1)).getDate().format(Utils.myFormatObj);
                    else
                        s += p.getDate().format(Utils.myFormatObj);
                    return s;
                })
                .collect(Collectors.toList());
    }

    /**
     * add a new request
     *
     * @param l1,l2- l1 sent a request to l2
     * @throws ServiceException when the request is already sent
     *                          when the id's don't exist in repoUser
     */
    public void addRequest(Long l1, Long l2) {

        Utilizator User1 = repoUsers.findOne(l1);
        Utilizator User2 = repoUsers.findOne(l2);
        if (User1 == null || User2 == null)
            throw new ServiceException("Trimitere cerere esuata: Id uri inexistente!\n");


        Tuple<Long, Long> idRequest = new Tuple<>(l1,l2);
        if ( l1.equals(l2))
            throw new ServiceException("Trimitere cerere prietenie invalida!Acelasi ID\n");

        if (repoFriendship.findOne(idRequest) != null){
            throw  new ServiceException("Trimitere cerere invalida! Prietenie existenta!!\n");
        }
        FriendRequest request = new FriendRequest();
        request.setId(idRequest);
        FriendRequest r = repoRequest.save(request);
        if (r != null)
            throw new ServiceException("Cerere de prietenie deja trimisa!\n");

    }

    /**
     * return a user's request list
     * @param id id of the user we want the list of requests
     * @return user's requests
     */
    public List<FriendRequest> userRequest(Long id){
        List<FriendRequest> friendRequests = new ArrayList<>();
        repoRequest.findAll().forEach(friendRequests::add);
        return friendRequests.stream()
                .filter(x->x.getId().getRight().equals(id))
                .collect(Collectors.toList());

    }

    /**
     * change status of a request
     * @param request - request we want to change the status
     */
    public void changeStatus(FriendRequest request){
        if (repoRequest.findOne(request.getId()) == null)
            throw  new ServiceException("Aceasta cerere nu a fost inregistrata!\n");
        String saved =repoRequest.findOne(request.getId()).getStatus();

        System.out.println(request.getStatus()+ "     "+saved);
        if(request.getStatus().equals("approved") && saved.equals("pending")){
            addFriend(request.getId().getLeft(),request.getId().getRight());
            System.out.println("STATUS"+request.getStatus());
            repoRequest.update(request);
        }else
            if (request.getStatus().equals("rejected") && saved.equals("pending"))
                repoRequest.update(request);
            else
                throw new ServiceException("Statusul cererii nu poate fi modificat!Cerere deja admisa/respinsa!\n");

    }


    /**
     * send a new
     * @param m message we want to add
     */
    public void sentNewMessage(Message m){
        if(repoUsers.findOne(m.getUserFrom()) == null || repoUsers.findOne(m.getUserTo()) == null)
            throw new ServiceException("Mesajul nu poate fi trimis!\nExpeditor/Destinatar invalid!\n");
        repoMessage.save(m);
    }

    /**
     *
     * @param id - user's id
     * @param mesaj - string
     */
    public void replyMessage(Long id, String mesaj){
        Message message = repoMessage.findOne(id);
        if (message == null)
            throw new ServiceException("Reply la mesaj invalid!!\n");
        Message reply = new Message(message.getUserTo(),message.getUserFrom(),id,mesaj);
        reply.setId(0L);
        repoMessage.save(reply);
    }

    /**
     *
     * @param id1 - first user's id
     * @param id2 - second user's id
     * @return list representing the conversation
     */
    public List<String> showConversation(Long id1 , Long id2){
        if(repoUsers.findOne(id1) == null || repoUsers.findOne(id2) == null)
            throw new ServiceException("Id-uri invalide!\n");
        ArrayList<Message> messages = new ArrayList<>() ;
        repoMessage.findAll().forEach(x->{
            if( (x.getUserFrom().equals(id1) && x.getUserTo().equals( id2)) || (x.getUserFrom().equals( id2) && x.getUserTo().equals(id1)))
                messages.add(x);
        });
        return messages.stream()
                .sorted(Comparator.comparing(Message::getDate))
                .map(x->"From: "+ x.getUserFrom() + " Date: "+ x.getDate().format(Utils.myFormatObj)+ " Mesaj: "+ x.getMesaj()+"\n")
                .collect(Collectors.toList());
    }

    /**
     *
     * @param l1 id user
     * @return list of received messeges from a user
     */
    public List<String> inboxUser(Long l1) {
        if (repoUsers.findOne(l1) == null)
            throw new ServiceException("Id invalis!\n");
        ArrayList<String> inbox = new ArrayList<>();
        repoMessage.findAll().forEach(x->{
            if ( x.getUserTo().equals(l1))
                inbox.add("ID: "+x.getId()+"From: "+ x.getUserFrom()+ "Messagge: "+x.getMesaj()+"\n");
        });
        return inbox;

    }

    /**
     *
     * @param raport list of strings
     * @param filename name of the file
     * @param cerinta string
     * @throws FileNotFoundException
     * @throws DocumentException
     */
    public void saveRaportToPDF(List<String> raport, String filename,String cerinta) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document,new FileOutputStream(filename + ".pdf"));
        document.open();

        Font bolt = new Font(Font.FontFamily.HELVETICA,18,Font.BOLDITALIC);
        Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
                Font.NORMAL, BaseColor.RED);
        Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
                Font.BOLD);
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
                Font.BOLD);
        Paragraph title = new Paragraph("Raport \n",bolt);

        document.add(title);

        document.addHeader("Nume raport",cerinta);
        Paragraph paragraph = new Paragraph( "\t\t"+cerinta+"\n",redFont);
        com.itextpdf.text.List list = new com.itextpdf.text.List(true,false,100);
        raport.forEach(x->list.add(x));
        Paragraph continut = new Paragraph("Continut\n",smallBold);
        continut.add(list);
        document.add(paragraph);
        document.add(continut);
        document.close();
    }

    public Object getUser(Long right) {
        return repoUsers.findOne(right);
    }
}