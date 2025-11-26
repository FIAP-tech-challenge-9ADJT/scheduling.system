package tech.challenge.scheduling.system.domain.entities;

import tech.challenge.scheduling.system.domain.valueobjects.UserId;
import tech.challenge.scheduling.system.domain.valueobjects.Name;
import tech.challenge.scheduling.system.domain.valueobjects.Email;
import tech.challenge.scheduling.system.domain.valueobjects.Login;
import tech.challenge.scheduling.system.domain.valueobjects.Password;
import java.time.LocalDateTime;
import java.util.Set;

public class Administrator extends User {
    public Administrator(UserId id, Name name, Email email, Login login, Password password, Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(id, name, email, login, password, roles, createdAt, updatedAt);
    }

    public static Administrator create(String name, String email, String login, String password) {
        throw new UnsupportedOperationException("Use createWithRoles para criar um admin com roles persistidas");
    }

    public static Administrator createWithRoles(String name, String email, String login, String password, Set<Role> roles) {
        return new Administrator(
            null,
            Name.of(name),
            Email.of(email),
            Login.of(login),
            Password.of(password),
            roles,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
