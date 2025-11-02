package tech.challenge.scheduling.system.domain.exceptions;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid credentials provided");
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }
}