package tech.challenge.scheduling.system.presentation.dtos.nurse;

public class NurseResponseDTO {
    public Long id;
    public String name;
    public String email;
    public String login;
    public String password;
    public String coren;
    public String role;

    public NurseResponseDTO(Long id, String name, String email, String login, String password, String coren, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.login = login;
        this.password = password;
        this.coren = coren;
        this.role = role;
    }
}
