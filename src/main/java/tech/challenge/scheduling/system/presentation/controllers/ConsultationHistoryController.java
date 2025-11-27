package tech.challenge.scheduling.system.presentation.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.challenge.scheduling.system.application.services.ConsultationHistoryService;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import tech.challenge.scheduling.system.presentation.dtos.consultation.ConsultationHistoryRequestDTO;
import tech.challenge.scheduling.system.presentation.dtos.consultation.ConsultationHistoryResponseDTO;
import tech.challenge.scheduling.system.infrastructure.security.MultiProfileUserDetails;

import java.util.List;

@RestController
@RequestMapping("/consultations")
public class ConsultationHistoryController {
    private final ConsultationHistoryService service;

    public ConsultationHistoryController(ConsultationHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ConsultationHistoryResponseDTO> create(@RequestBody ConsultationHistoryRequestDTO dto) {
        ConsultationHistory entity = toEntity(dto);
        ConsultationHistory saved = service.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDTO(saved));
    }

    @GetMapping
    public ResponseEntity<List<ConsultationHistoryResponseDTO>> getAll(@AuthenticationPrincipal MultiProfileUserDetails user) {
        List<ConsultationHistory> all = service.findAll();
        List<ConsultationHistory> filtered = user.getRole().equalsIgnoreCase("PATIENT")
            ? all.stream().filter(c -> c.getPatientId().equals(user.getId())).toList()
            : all;
        return ResponseEntity.ok(filtered.stream().map(this::toResponseDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationHistoryResponseDTO> getById(@PathVariable Long id, @AuthenticationPrincipal MultiProfileUserDetails user) {
        return service.findById(id)
            .filter(c -> !user.getRole().equalsIgnoreCase("PATIENT") || c.getPatientId().equals(user.getId()))
            .map(this::toResponseDTO)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ConsultationHistoryResponseDTO>> getByPatientId(@PathVariable Long patientId, @AuthenticationPrincipal MultiProfileUserDetails user) {
        if (user.getRole().equalsIgnoreCase("PATIENT") && !user.getId().equals(patientId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<ConsultationHistory> consultations = service.findByPatientId(patientId);
        List<ConsultationHistoryResponseDTO> dtos = consultations.stream().map(this::toResponseDTO).toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationHistoryResponseDTO> update(@PathVariable Long id, @RequestBody ConsultationHistoryRequestDTO dto) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        ConsultationHistory entity = toEntity(dto);
        entity.setId(id);
        ConsultationHistory updated = service.save(entity);
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ConsultationHistory toEntity(ConsultationHistoryRequestDTO dto) {
        ConsultationHistory entity = new ConsultationHistory();
        entity.setPatientId(dto.patientId());
        entity.setDoctorId(dto.doctorId());
        entity.setNurseId(dto.nurseId());
        entity.setDateTime(dto.dateTime());
        entity.setDescription(dto.description());
        entity.setNotes(dto.notes());
        return entity;
    }

    private ConsultationHistoryResponseDTO toResponseDTO(ConsultationHistory entity) {
        return new ConsultationHistoryResponseDTO(
            entity.getId(),
            entity.getPatientId(),
            entity.getDoctorId(),
            entity.getNurseId(),
            entity.getDateTime(),
            entity.getDescription(),
            entity.getNotes()
        );
    }
}
