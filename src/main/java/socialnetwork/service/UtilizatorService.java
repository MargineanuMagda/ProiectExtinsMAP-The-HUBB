package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.Repository;


import java.util.*;
import java.util.stream.Collectors;

//import static jdk.vm.ci.sparc.SPARC.f1;

/**
 * GRASP Controller
 */
public class UtilizatorService {

    //Service has 2 types of repo: repoUsers ans a repoFrienship
    private final Repository<Long, Utilizator> repoUsers;
    private final Repository<Tuple<Long, Long>, Prietenie> repoFriendship;

    //Graph structure used for finding comunities
    //dictionar ce creeaza o relatie de 1-1 intre noduri si id urile userilor
    private final Map<Long, Integer> map = new HashMap<>();
    private final Map<Integer, Long> reverseMap = new HashMap<>();
    private Boolean[][] adj;//matrice de adiacenta
    private int nodes;//numar noduri
    private Boolean[] visited;



    public UtilizatorService(Repository<Long, Utilizator> repo, Repository<Tuple<Long, Long>, Prietenie> repo1) {
        this.repoUsers = repo;
        this.repoFriendship = repo1;
        loadFriends();
    }

    /**
     * private method which load user's list of friends based on the data in the repoFriendship
     */
    private void loadFriends() {
        repoFriendship.findAll().forEach(x -> {
            Utilizator user1 = repoUsers.findOne(x.getId().getLeft());
            Utilizator user2 = repoUsers.findOne(x.getId().getRight());
            user1.addFriend(user2);
            user2.addFriend(user1);
        });
    }


    /**
     * add a new user
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
     *          -delete all the friendships which contains entity' Id from RepoFriendship
     *          -delete all the
     *          -the delete the user from repoUser
     * @param ID-delete the user which contains id
     * @throws ServiceException when
     *                  id does not exist in repo
     */
    public void deleteUtilizator(Long ID) {
        Utilizator userRemoved = repoUsers.findOne(ID);
        if (userRemoved == null)
            throw new ServiceException("Service exception: ID invalid pentru stergere!\n");

        List<Utilizator> friends = userRemoved.getFriends();
        friends.forEach(x -> {
            if (ID < x.getId())
                repoFriendship.delete(new Tuple<>(ID, x.getId()));
            else
                repoFriendship.delete(new Tuple<>(x.getId(), ID));
            x.removeFriend(userRemoved);
        });

        repoUsers.delete(ID);

    }

    /**
     * add a new firendship between 2 users
     * @param ID1- id of first user
     * @param ID2- second user's id
     * @throws ServiceException when
     *                  one of the IDs does not exist in repoUsers(or both)
     *                  the IDs are equal
     *                  the friendship exists in RepoFriendship
     *
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

        User1.addFriend(User2);
        User2.addFriend(User1);
        System.out.println("User1 " + User1 + "\nUser2 " + User2);


    }

    /**
     * delete a friendship
     * @param ID1 - first id
     * @param ID2 - second is
     *@throws ServiceException when
      *                  one of the IDs does not exist in repoUsers(or both)
      *                  the IDs are equal
      *                  the friendship doesn t exist
     */
    public void removeFriend(Long ID1, Long ID2) {
        Utilizator User1 = repoUsers.findOne(ID1);
        Utilizator User2 = repoUsers.findOne(ID2);
        if (User1 == null || User2 == null)
            throw new ServiceException("Adaugare esuata: Id uri inexistente!\n");

        User1.removeFriend(User2);
        User2.removeFriend(User1);

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
     *
     * @return  list of users
     */
    public Iterable<Utilizator> getAllUsers() {
        return repoUsers.findAll();
    }

    /**
     *
     * @return list of friendships
     */
    public Iterable<Prietenie> getAllPrietenii() {
        return repoFriendship.findAll();
    }


    /**
     * initializeaza graful prieteniilor
     *              -initializeaza matricea de adiacenta
     *              -vectorul vizitat
     *              -creeaza o relatie 1-1 inre id-uri si noduri
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
     * @param nod - int
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
     * @return  list of friends
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

    public List<String> getUserFriends(long l1){
         return repoUsers.findOne(l1).getFriends().stream()
                .map(x->{

                     return "Prenume:\t"+x.getFirstName()+"|Nume:\t" +x.getLastName()+"|Data:\t"+repoFriendship.findOne(new Tuple<>(l1,x.getId())).getDate();
                })
                .collect(Collectors.toList());
    }

    public List<String> getUserFriendsByMonth(Long l1, Integer month) {
        return repoUsers.findOne(l1).getFriends().stream()
                .filter(x->repoFriendship.findOne(new Tuple<>(l1,x.getId())).getDate().getMonthValue() == month)
                .map(x->"Prenume:\t"+x.getFirstName()+"|Nume:\t" +x.getLastName()+"|Data:\t"+repoFriendship.findOne(new Tuple<>(l1,x.getId())).getDate())
                .collect(Collectors.toList());
    }

    ///TO DO: add other methods
}
