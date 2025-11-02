package tech.challenge.scheduling.system.presentation.dtos.user;

import tech.challenge.scheduling.system.presentation.dtos.role.RoleResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String login,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<RoleResponseDTO> roles) {
}