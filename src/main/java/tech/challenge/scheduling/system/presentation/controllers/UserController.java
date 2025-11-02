package tech.challenge.scheduling.system.presentation.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.challenge.scheduling.system.presentation.dtos.user.UserResponseDTO;
import tech.challenge.scheduling.system.presentation.mappers.UserDtoMapper;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.UserJpaEntity;
import tech.challenge.scheduling.system.application.services.UserApplicationService;
import tech.challenge.scheduling.system.domain.valueobjects.UserId;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getProfile(@AuthenticationPrincipal UserJpaEntity authenticatedUser) {
        var user = userApplicationService.findUser(UserId.of(authenticatedUser.getId()));
        return ResponseEntity.ok(UserDtoMapper.toResponseDto(user));
    }
}