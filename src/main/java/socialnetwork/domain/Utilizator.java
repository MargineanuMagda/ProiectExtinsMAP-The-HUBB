package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private final String firstName;
    private final String lastName;
    private final List<Utilizator> friends = new ArrayList<>();
    public Utilizator(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

    }

    public String getFirstName() {
        return firstName;
    }



    public String getLastName() {
        return lastName;
    }


    /**
     * return the list of friends
     * @return friends- a list of users which are friend with the current user
     */
    public List<Utilizator> getFriends() {
        return friends;
    }

    /**
     * add a new friend
     * @param newFriend- a new user which is a new friend of the current user
     */
    public void addFriend (Utilizator newFriend){
        friends.add(newFriend);
    }

    /**
     * remove a friend from the current user's list of friends
     * @param oldFriend- user which will be removed
     */
    public void removeFriend(Utilizator oldFriend){
        friends.remove(oldFriend);
    }

    @Override
    public String toString() {


        return "ID: " + getId() +
                "\tFirstName='" + firstName + '\'' +
                ", LastName='" + lastName + '\''
                ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}