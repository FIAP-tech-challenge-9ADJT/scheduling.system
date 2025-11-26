package tech.challenge.scheduling.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.PatientJpaEntity;

public interface PatientJpaRepository extends JpaRepository<PatientJpaEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    PatientJpaEntity findByEmail(String email);
    PatientJpaEntity findByLogin(String login);
}
