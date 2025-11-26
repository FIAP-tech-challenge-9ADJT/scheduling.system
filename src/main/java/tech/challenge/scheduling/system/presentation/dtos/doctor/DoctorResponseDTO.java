package tech.challenge.scheduling.system.presentation.dtos.doctor;

public class DoctorResponseDTO {
    public Long id;
    public String name;
    public String email;
    public String login;
    public String password;
    public String crm;
    public String role;

    public DoctorResponseDTO(Long id, String name, String email, String login, String password, String crm, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.crm = crm;
        this.role = role;
    }
}
