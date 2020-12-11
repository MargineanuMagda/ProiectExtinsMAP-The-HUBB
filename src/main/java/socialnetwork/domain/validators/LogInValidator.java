package socialnetwork.domain.validators;

import socialnetwork.domain.LogIn;

public class LogInValidator implements Validator<LogIn> {

    @Override
    public void validate(LogIn entity) throws ValidationException {
        if(entity.getId().getLeft() == null)
            throw new ValidationException("username null!\n");
        if(entity.getId().getRight() == null && entity.getId().getRight().length()<6)
            throw new ValidationException("parola invalida!!\n");
    }
}
