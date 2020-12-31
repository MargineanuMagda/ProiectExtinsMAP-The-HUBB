package socialnetwork.domain.validators;

import socialnetwork.domain.Event;

import java.time.LocalDateTime;

public class EventValidator implements Validator<Event> {

    @Override
    public void validate(Event entity) throws ValidationException {
        if(entity.getName().equals(""))
            throw new ValidationException("Invalid name!!");
        if(entity.getDate().isBefore(LocalDateTime.now()))
            throw new ValidationException("Please put a dat after current date!");
    }
}
