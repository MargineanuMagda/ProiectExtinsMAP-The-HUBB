package socialnetwork.domain;

public class LogIn extends Entity<Tuple<String,String>>{
    private Long idGenerat;


    public LogIn(Long idGenerat) {
        this.idGenerat = idGenerat;
    }

    public Long getIdGenerat() {
        return idGenerat;
    }

    public void setIdGenerat(Long idGenerat) {
        this.idGenerat = idGenerat;
    }

    @Override
    public String toString() {
        return "Username: " + getId().getLeft()+"  Passworld: "+ getId().getRight();
    }
}
