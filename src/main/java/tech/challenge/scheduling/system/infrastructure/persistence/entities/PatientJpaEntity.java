package tech.challenge.scheduling.system.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String login;
    private String password;
    private String cpf;
    private String address;
    private String addressNumber;
    private String city;
    private String state;
    private String postalCode;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
