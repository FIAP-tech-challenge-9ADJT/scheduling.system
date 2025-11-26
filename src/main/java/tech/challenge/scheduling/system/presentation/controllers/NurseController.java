package tech.challenge.scheduling.system.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.NurseJpaEntity;
import tech.challenge.scheduling.system.application.services.NurseApplicationService;
import tech.challenge.scheduling.system.presentation.dtos.nurse.NurseResponseDTO;

@RestController
@RequestMapping("/nurses")
public class NurseController {

    private final NurseApplicationService nurseService;

    public NurseController(NurseApplicationService nurseService) {
        this.nurseService = nurseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<NurseResponseDTO> getNurseById(@PathVariable Long id) {
        NurseJpaEntity nurse = nurseService.getNurseById(id);
        return nurse != null ? ResponseEntity.ok(toResponseDTO(nurse)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<NurseResponseDTO> createNurse(@RequestBody NurseRequest request) {
        NurseJpaEntity nurse = nurseService.createNurse(request.id(), request.name(), request.email(), request.login(), request.password(), request.coren());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(nurse));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NurseResponseDTO> updateNurse(@PathVariable Long id, @RequestBody NurseRequest request) {
        NurseJpaEntity updated = nurseService.updateNurse(id, request.name(), request.email(), request.login(), request.password(), request.coren());
        return updated != null ? ResponseEntity.ok(toResponseDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNurse(@PathVariable Long id) {
        boolean deleted = nurseService.deleteNurse(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public static NurseResponseDTO toResponseDTO(NurseJpaEntity nurse) {
        return new NurseResponseDTO(
            nurse.getId(),
            nurse.getName(),
            nurse.getEmail(),
            nurse.getLogin(),
            nurse.getPassword(),
            nurse.getCoren(),
            nurse.getRole()
        );
    }

    public record NurseRequest(Long id, String name, String email, String login, String password, String coren) {}
}
