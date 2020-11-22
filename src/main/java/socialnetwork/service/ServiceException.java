package socialnetwork.service;

/**
 * class for service exception
 */
public class ServiceException extends RuntimeException{
    public ServiceException() {
    }

    public ServiceException(String message) {
        super(message);
    }
}
