package socialnetwork.domain;

import java.time.LocalDateTime;

import static socialnetwork.utils.Utils.myFormatObj;

public class FriendRequest extends Entity<Tuple<Long,Long>>{
    String status;
    LocalDateTime data;

    public FriendRequest(){
        status = "pending";
        data =LocalDateTime.now();
    }

    public FriendRequest(String status,LocalDateTime data) {
        this.status = status;
        this.data = data;
    }

    @Override
    public String toString() {
        return "from: "  + getId().getLeft()+ " to: "+ getId().getRight()+  " status: "+ status+" data: "+data.format(myFormatObj);
    }

    public String getStatus() {
        return status;
    }
    public LocalDateTime getData(){
        return data;
    }
    public void setStatus(String statusNew ){
        this.status = statusNew;
    }
}
