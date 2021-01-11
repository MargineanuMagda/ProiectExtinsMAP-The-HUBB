package socialnetwork.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private final String firstName;
    private final String lastName;
    private final List<Utilizator> friends = new ArrayList<>();
    private List<String> bio = Arrays.asList("","","","","","");

    public Utilizator(String firstName, String lastName ) {
        this.firstName = firstName;
        this.lastName = lastName;

    }
    public void setBio(List<String> list){
        bio=list;
    }
    public String getSchool(){
        return bio.get(0);
    }
    public void setSchool(String school){
        bio.set(0, school);
    }
    public String getLiving(){
        return bio.get(1);
    }
    public void setLiving(String liv){
        bio.set(1, liv);
    }

    public String getFrom(){
        return bio.get(2);
    }
    public void setFrom(String from){
        bio.set(2, from);
    }
    public String getHobby(){
        return bio.get(3);
    }
    public void setHobby(String s){
        bio.set(3, s);
    }
    public String getSex(){
        return bio.get(4);
    }
    public void setSex(String s){
        bio.set(4, s);
    }
    public String getAbout(){
        return bio.get(5);
    }
    public void setAbout(String s){
        bio.set(5, s);
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


        return firstName + " " + lastName;
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