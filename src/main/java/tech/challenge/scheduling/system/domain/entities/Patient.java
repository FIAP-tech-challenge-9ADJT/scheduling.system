package tech.challenge.scheduling.system.domain.entities;

import tech.challenge.scheduling.system.domain.valueobjects.*;

import java.time.LocalDateTime;
import java.util.Set;

public class Patient extends User {
    private final String cpf;
    private final String address;
    private final String addressNumber;
    private final String city;
    private final String state;
    private final String postalCode;

    public Patient(UserId id, Name name, Email email, Login login, Password password, Set<Role> roles,
                   LocalDateTime createdAt, LocalDateTime updatedAt,
                   String cpf, String address, String addressNumber, String city, String state, String postalCode) {
        super(id, name, email, login, password, roles, createdAt, updatedAt);
        this.cpf = cpf;
        this.address = address;
        this.addressNumber = addressNumber;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }

    public static Patient create(String name, String email, String login, String password,
                                String cpf, String address, String addressNumber, String city, String state, String postalCode) {
        return new Patient(
                null,
                Name.of(name),
                Email.of(email),
                Login.of(login),
                Password.of(password),
                Set.of(Role.create(Role.RoleName.PATIENT)),
                LocalDateTime.now(),
                LocalDateTime.now(),
                cpf, address, addressNumber, city, state, postalCode
        );
    }

    public String getCpf() { return cpf; }
    public String getAddress() { return address; }
    public String getAddressNumber() { return addressNumber; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
}
