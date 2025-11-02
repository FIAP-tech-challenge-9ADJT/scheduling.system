package tech.challenge.scheduling.system.domain.repositories;

import tech.challenge.scheduling.system.domain.entities.Role;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(Role.RoleName name);
}