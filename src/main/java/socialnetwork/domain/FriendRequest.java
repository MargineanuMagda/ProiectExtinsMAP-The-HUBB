package socialnetwork.domain;

public class FriendRequest extends Entity<Tuple<Long,Long>>{
    String status;

    public FriendRequest(){
        status = "pending";
    }

    public FriendRequest(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return super.toString() + "status: "+ status;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String statusNew ){
        this.status = statusNew;
    }
}
