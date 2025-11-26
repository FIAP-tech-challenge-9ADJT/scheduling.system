package tech.challenge.scheduling.system.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.AdminJpaEntity;

@Repository
public interface AdminJpaRepository extends JpaRepository<AdminJpaEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByLogin(String login);
    AdminJpaEntity findByEmail(String email);
    AdminJpaEntity findByLogin(String login);
}
