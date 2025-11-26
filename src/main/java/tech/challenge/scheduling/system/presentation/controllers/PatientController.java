package tech.challenge.scheduling.system.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.PatientJpaEntity;
import tech.challenge.scheduling.system.application.services.PatientApplicationService;
import tech.challenge.scheduling.system.presentation.dtos.patient.PatientResponseDTO;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientApplicationService patientService;

    public PatientController(PatientApplicationService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatientById(@PathVariable Long id) {
        PatientJpaEntity patient = patientService.getPatientById(id);
        return patient != null ? ResponseEntity.ok(toResponseDTO(patient)) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@RequestBody PatientRequest request) {
        PatientJpaEntity patient = patientService.createPatient(
            request.id(), request.name(), request.email(), request.login(), request.password(),
            request.cpf(), request.address(), request.addressNumber(),
            request.city(), request.state(), request.postalCode()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(patient));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(@PathVariable Long id, @RequestBody PatientRequest request) {
        PatientJpaEntity updated = patientService.updatePatient(id, request.name(), request.email(), request.login(), request.password(),
            request.cpf(), request.address(), request.addressNumber(), request.city(), request.state(), request.postalCode());
        return updated != null ? ResponseEntity.ok(toResponseDTO(updated)) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        boolean deleted = patientService.deletePatient(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    public static PatientResponseDTO toResponseDTO(PatientJpaEntity patient) {
        return new PatientResponseDTO(
            patient.getId(),
            patient.getName(),
            patient.getEmail(),
            patient.getLogin(),
            patient.getPassword(),
            patient.getCpf(),
            patient.getAddress(),
            patient.getAddressNumber(),
            patient.getCity(),
            patient.getState(),
            patient.getPostalCode(),
            patient.getRole()
        );
    }

    public record PatientRequest(Long id, String name, String email, String login, String password,
        String cpf, String address, String addressNumber, String city, String state, String postalCode
    ) {}
}
