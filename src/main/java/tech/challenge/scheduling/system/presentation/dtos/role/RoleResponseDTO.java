package tech.challenge.scheduling.system.presentation.dtos.role;

import tech.challenge.scheduling.system.infrastructure.persistence.entities.RoleName;

public record RoleResponseDTO(
                Long id,
                RoleName name) {
}