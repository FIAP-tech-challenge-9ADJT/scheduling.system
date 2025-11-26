package tech.challenge.scheduling.system.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.AdminJpaEntity;
import tech.challenge.scheduling.system.application.services.AdministratorApplicationService;
import tech.challenge.scheduling.system.presentation.dtos.admin.AdministratorResponseDTO;

@RestController
@RequestMapping("/admins")
public class AdministratorController {

    private final AdministratorApplicationService adminService;

    public AdministratorController(AdministratorApplicationService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdministratorResponseDTO> getAdminById(@PathVariable Long id) {
        AdminJpaEntity admin = adminService.getAdminById(id);
        return admin != null ? ResponseEntity.ok(toResponseDTO(admin)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<AdministratorResponseDTO> createAdmin(@RequestBody AdminRequest request) {
        AdminJpaEntity admin = adminService.createAdmin(request.id(), request.name(), request.email(), request.login(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(admin));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministratorResponseDTO> updateAdmin(@PathVariable Long id, @RequestBody AdminRequest request) {
        AdminJpaEntity updated = adminService.updateAdmin(id, request.name(), request.email(), request.login(), request.password());
        return updated != null ? ResponseEntity.ok(toResponseDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        boolean deleted = adminService.deleteAdmin(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public static AdministratorResponseDTO toResponseDTO(AdminJpaEntity admin) {
        return new AdministratorResponseDTO(
            admin.getId(),
            admin.getName(),
            admin.getEmail(),
            admin.getLogin(),
            admin.getPassword(),
            admin.getRole()
        );
    }

    public record AdminRequest(Long id, String name, String email, String login, String password) {}
}
