package tech.challenge.scheduling.system.domain.entities;

import tech.challenge.scheduling.system.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Set;

public class Nurse extends User {
    private final String coren;

    public Nurse(UserId id, Name name, Email email, Login login, Password password, Set<Role> roles,
                 LocalDateTime createdAt, LocalDateTime updatedAt, String coren) {
        super(id, name, email, login, password, roles, createdAt, updatedAt);
        this.coren = coren;
    }

    public static Nurse create(String name, String email, String login, String password, String coren) {
        if (coren == null || coren.length() != 7 || !coren.matches("\\d{7}")) {
            throw new IllegalArgumentException("COREN deve conter exatamente 7 dígitos numéricos");
        }
        return new Nurse(
                null,
                Name.of(name),
                Email.of(email),
                Login.of(login),
                Password.of(password),
                Set.of(Role.create(Role.RoleName.NURSE)),
                LocalDateTime.now(),
                LocalDateTime.now(),
                coren
        );
    }

    public String getCoren() { return coren; }
}
