package tech.challenge.scheduling.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.DoctorJpaEntity;

public interface DoctorJpaRepository extends JpaRepository<DoctorJpaEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    DoctorJpaEntity findByEmail(String email);
    DoctorJpaEntity findByLogin(String login);
}
