package tech.challenge.scheduling.system.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.DoctorJpaEntity;
import tech.challenge.scheduling.system.application.services.DoctorApplicationService;
import tech.challenge.scheduling.system.presentation.dtos.doctor.DoctorResponseDTO;

@RestController
@RequestMapping("/doctors")
public class DoctorController {

    private final DoctorApplicationService doctorService;

    public DoctorController(DoctorApplicationService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> getDoctorById(@PathVariable Long id) {
        DoctorJpaEntity doctor = doctorService.getDoctorById(id);
        return doctor != null ? ResponseEntity.ok(toResponseDTO(doctor)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DoctorResponseDTO> createDoctor(@RequestBody DoctorRequest request) {
        DoctorJpaEntity doctor = doctorService.createDoctor(request.id(), request.name(), request.email(), request.login(), request.password(), request.crm());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(doctor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponseDTO> updateDoctor(@PathVariable Long id, @RequestBody DoctorRequest request) {
        DoctorJpaEntity updated = doctorService.updateDoctor(id, request.name(), request.email(), request.login(), request.password(), request.crm());
        return updated != null ? ResponseEntity.ok(toResponseDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        boolean deleted = doctorService.deleteDoctor(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public static DoctorResponseDTO toResponseDTO(DoctorJpaEntity doctor) {
        return new DoctorResponseDTO(
            doctor.getId(),
            doctor.getName(),
            doctor.getEmail(),
            doctor.getLogin(),
            doctor.getPassword(),
            doctor.getCrm(),
            doctor.getRole()
        );
    }

    public record DoctorRequest(Long id, String name, String email, String login, String password, String crm) {}
}
