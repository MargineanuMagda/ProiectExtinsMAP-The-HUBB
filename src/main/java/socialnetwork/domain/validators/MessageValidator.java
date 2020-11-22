package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

public class MessageValidator implements Validator<Message> {

    @Override
    public void validate(Message entity) throws ValidationException {
        if( entity.getMesaj().equals(""))
            throw new ValidationException("Mesaajul nu poate fi null!\n");
    }
}
