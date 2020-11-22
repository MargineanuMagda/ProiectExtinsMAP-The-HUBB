package socialnetwork.domain.validators;

/**
 * validate an entuty
 * @param <T>-an entity of type T
 */
public interface Validator<T> {
    /**
     *
     * @param entity- entity we want to validate
     * @throws ValidationException
     */
    void validate(T entity) throws ValidationException;
}