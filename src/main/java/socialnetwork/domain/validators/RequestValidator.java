package socialnetwork.domain.validators;

import socialnetwork.domain.FriendRequest;


public class RequestValidator implements Validator<FriendRequest> {

    public void validate(FriendRequest request){
        if ( !request.getStatus().equals("pending") && !request.getStatus().equals("approved") && !request.getStatus().equals("rejected"))
            throw new ValidationException("Eroare de validare cerere! Statusul trebuie sa fie unul dintre pending/approved/rejected");
    }
}
