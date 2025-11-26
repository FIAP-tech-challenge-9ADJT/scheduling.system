package tech.challenge.scheduling.system.presentation.dtos.patient;

public class PatientResponseDTO {
    public Long id;
    public String name;
    public String email;
    public String login;
    public String password;
    public String cpf;
    public String address;
    public String addressNumber;
    public String city;
    public String state;
    public String postalCode;
    public String role;

    public PatientResponseDTO(Long id, String name, String email, String login, String password, String cpf, String address, String addressNumber, String city, String state, String postalCode, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.cpf = cpf;
        this.address = address;
        this.addressNumber = addressNumber;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.role = role;
    }
}
