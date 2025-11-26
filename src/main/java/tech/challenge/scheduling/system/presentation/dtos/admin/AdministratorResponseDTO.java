package tech.challenge.scheduling.system.presentation.dtos.admin;

public class AdministratorResponseDTO {
    public Long id;
    public String name;
    public String email;
    public String login;
    public String password;
    public String role;

    public AdministratorResponseDTO(Long id, String name, String email, String login, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.role = role;
    }
}
