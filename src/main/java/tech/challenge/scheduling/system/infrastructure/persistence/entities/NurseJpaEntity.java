package tech.challenge.scheduling.system.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "nurses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NurseJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String login;
    private String password;
    private String coren;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
