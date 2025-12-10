package tech.challenge.scheduling.system.presentation.controllers;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import tech.challenge.scheduling.system.application.services.*;
import tech.challenge.scheduling.system.infrastructure.messaging.ConsultationNotificationPublisher;
import tech.challenge.scheduling.system.infrastructure.messaging.ConsultationNotificationMessage;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.*;
import tech.challenge.scheduling.system.infrastructure.security.MultiProfileUserDetails;
import tech.challenge.scheduling.system.presentation.dtos.graphql.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class ConsultationGraphQLController {

    private final ConsultationHistoryService consultationService;
    private final PatientApplicationService patientService;
    private final DoctorApplicationService doctorService;
    private final NurseApplicationService nurseService;
    private final ConsultationNotificationPublisher notificationPublisher;

    public ConsultationGraphQLController(
            ConsultationHistoryService consultationService,
            PatientApplicationService patientService,
            DoctorApplicationService doctorService,
            NurseApplicationService nurseService,
            ConsultationNotificationPublisher notificationPublisher) {
        this.consultationService = consultationService;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.nurseService = nurseService;
        this.notificationPublisher = notificationPublisher;
    }

    // Queries principais para histórico médico (REQUISITO DA ESPECIFICAÇÃO)
    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and #patientId == authentication.principal.id)")
    public List<ConsultationHistory> consultationsByPatient(
            @Argument Long patientId,
            @AuthenticationPrincipal MultiProfileUserDetails user) {
        return consultationService.findByPatientId(patientId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public List<ConsultationHistory> consultationsByDoctor(@Argument Long doctorId) {
        return consultationService.findByDoctorId(doctorId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public List<ConsultationHistory> consultationsByDateRange(
            @Argument String startDate, 
            @Argument String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        return consultationService.findByDateRange(start, end);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE') or (hasRole('PATIENT') and (#patientId == null or #patientId == authentication.principal.id))")
    public List<ConsultationHistory> futureConsultations(
            @Argument Long patientId,
            @AuthenticationPrincipal MultiProfileUserDetails user) {
        
        // Se for paciente, só pode ver suas próprias consultas
        Long actualPatientId = user.getRole().equals("PATIENT") ? user.getId() : patientId;
        return consultationService.findFutureByPatientId(actualPatientId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'PATIENT')")
    public List<ConsultationHistory> consultations(
            @Argument ConsultationFilterDTO filter,
            @AuthenticationPrincipal MultiProfileUserDetails user) {

        if (filter == null) {
            filter = new ConsultationFilterDTO(null, null, null, null, null, null);
        }
        
        // Aplicar filtro de segurança para pacientes
        if (user.getRole().equals("PATIENT")) {
            filter = filter.withPatientId(user.getId());
        }
        
        return consultationService.findWithFilter(filter);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE', 'PATIENT')")
    public Optional<ConsultationHistory> consultation(
            @Argument Long id,
            @AuthenticationPrincipal MultiProfileUserDetails user) {
        
        Optional<ConsultationHistory> consultation = consultationService.findById(id);
        
        // Se for paciente, só pode ver suas próprias consultas
        if (user.getRole().equals("PATIENT") && consultation.isPresent()) {
            if (!consultation.get().getPatientId().equals(user.getId())) {
                return Optional.empty();
            }
        }
        
        return consultation;
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public List<TimeSlotDTO> availableSlots(@Argument Long doctorId, @Argument String date) {
        return consultationService.findAvailableSlots(doctorId, date);
    }

    // Mutations para agendamento (REQUISITO DA ESPECIFICAÇÃO)
    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'NURSE', 'DOCTOR')")
    public ConsultationHistory createConsultation(@Argument CreateConsultationInputDTO input) {
        ConsultationHistory consultation = new ConsultationHistory();
        consultation.setPatientId(input.patientId());
        consultation.setDoctorId(input.doctorId());
        consultation.setNurseId(input.nurseId());
        consultation.setDateTime(LocalDateTime.parse(input.dateTime()));
        consultation.setDescription(input.description());
        consultation.setNotes(input.notes());
        
        ConsultationHistory saved = consultationService.save(consultation);
        PatientJpaEntity patient = patientService.getPatientById(saved.getPatientId());
        ConsultationNotificationMessage message = new ConsultationNotificationMessage(
                saved.getId(),
                saved.getDateTime(),
                saved.getPatientId(),
                patient != null ? patient.getName() : null,
                patient != null ? patient.getEmail() : null
        );
        notificationPublisher.publish(message);
        return saved;
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ConsultationHistory updateConsultation(
            @Argument Long id, 
            @Argument UpdateConsultationInputDTO input) {
        return consultationService.update(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public Boolean cancelConsultation(@Argument Long id, @Argument String reason) {
        return consultationService.cancel(id, reason);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'NURSE')")
    public ConsultationHistory rescheduleConsultation(@Argument Long id, @Argument String newDateTime) {
        return consultationService.reschedule(id, LocalDateTime.parse(newDateTime));
    }

    // Schema Mappings para resolver relacionamentos (lazy loading)
    @SchemaMapping
    public PatientJpaEntity patient(ConsultationHistory consultation) {
        return patientService.getPatientById(consultation.getPatientId());
    }

    @SchemaMapping
    public DoctorJpaEntity doctor(ConsultationHistory consultation) {
        return doctorService.getDoctorById(consultation.getDoctorId());
    }

    @SchemaMapping
    public NurseJpaEntity nurse(ConsultationHistory consultation) {
        if (consultation.getNurseId() != null) {
            return nurseService.getNurseById(consultation.getNurseId());
        }
        return null;
    }
}
