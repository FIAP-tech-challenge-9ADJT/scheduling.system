package tech.challenge.scheduling.system.domain.exceptions;

public class AccessDeniedException extends DomainException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}