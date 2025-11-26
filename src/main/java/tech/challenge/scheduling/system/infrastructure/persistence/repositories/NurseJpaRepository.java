package tech.challenge.scheduling.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.NurseJpaEntity;

public interface NurseJpaRepository extends JpaRepository<NurseJpaEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    NurseJpaEntity findByEmail(String email);
    NurseJpaEntity findByLogin(String login);
}
