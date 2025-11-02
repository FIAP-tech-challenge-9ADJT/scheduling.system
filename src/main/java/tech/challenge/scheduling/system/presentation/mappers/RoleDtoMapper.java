package tech.challenge.scheduling.system.presentation.mappers;

import tech.challenge.scheduling.system.domain.entities.Role;
import tech.challenge.scheduling.system.presentation.dtos.role.RoleResponseDTO;

public class RoleDtoMapper {

    public static RoleResponseDTO toResponseDto(Role role) {
        return new RoleResponseDTO(
                role.getId(),
                tech.challenge.scheduling.system.infrastructure.persistence.entities.RoleName
                        .valueOf(role.getName().name()));
    }
}