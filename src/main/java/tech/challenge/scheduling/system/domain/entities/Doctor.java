package tech.challenge.scheduling.system.domain.entities;

import tech.challenge.scheduling.system.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Set;

public class Doctor extends User {
    private final String crm;

    public Doctor(UserId id, Name name, Email email, Login login, Password password, Set<Role> roles,
                  LocalDateTime createdAt, LocalDateTime updatedAt, String crm) {
        super(id, name, email, login, password, roles, createdAt, updatedAt);
        this.crm = crm;
    }

    public static Doctor create(String name, String email, String login, String password, String crm) {
        if (crm == null || crm.length() != 6 || !crm.matches("\\d{6}")) {
            throw new IllegalArgumentException("CRM deve conter exatamente 6 dígitos numéricos");
        }
        return new Doctor(
                null,
                Name.of(name),
                Email.of(email),
                Login.of(login),
                Password.of(password),
                Set.of(Role.create(Role.RoleName.DOCTOR)),
                LocalDateTime.now(),
                LocalDateTime.now(),
                crm
        );
    }

    public String getCrm() { return crm; }
}
