package socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static socialnetwork.utils.Utils.myFormatObj;

public class FriendRequest extends Entity<Tuple<Long,Long>> implements Comparable<FriendRequest>{
    String status;
    LocalDate data;

    public FriendRequest(){
        status = "pending";
        data =LocalDate.now();
    }

    public FriendRequest(String status,LocalDate data) {
        this.status = status;
        this.data = data;
    }

    @Override
    public String toString() {
        return "from: "  + getId().getLeft()+ " to: "+ getId().getRight()+  " status: "+ status+" data: "+data;
    }

    public String getStatus() {
        return status;
    }
    public LocalDate getData(){
        return data;
    }
    public void setStatus(String statusNew ){
        this.status = statusNew;
    }

    @Override
    public int compareTo(FriendRequest o) {
        if(getId().getLeft()==o.getId().getLeft())
            return (int) (getId().getRight()-o.getId().getRight());
        return (int) (getId().getLeft()-o.getId().getLeft());
    }
}
