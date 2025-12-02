package tech.challenge.scheduling.system.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import tech.challenge.scheduling.system.exceptions.ResourceNotFoundException;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.ConsultationHistoryRepository;
import tech.challenge.scheduling.system.presentation.dtos.graphql.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConsultationHistoryService {
    private final ConsultationHistoryRepository repository;

    public ConsultationHistoryService(ConsultationHistoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ConsultationHistory save(ConsultationHistory consultation) {
        return repository.save(consultation);
    }

    @Transactional(readOnly = true)
    public Optional<ConsultationHistory> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ConsultationHistory> findByPatientId(Long patientId) {
        return repository.findByPatientId(patientId);
    }

    @Transactional(readOnly = true)
    public List<ConsultationHistory> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // Novos métodos para GraphQL
    @Transactional(readOnly = true)
    public List<ConsultationHistory> findByDoctorId(Long doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    @Transactional(readOnly = true)
    public List<ConsultationHistory> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByDateTimeBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public List<ConsultationHistory> findFutureByPatientId(Long patientId) {
        return repository.findByPatientIdAndDateTimeAfter(patientId, LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public ConsultationConnectionDTO findWithPagination(ConsultationFilterDTO filter) {
        List<ConsultationHistory> consultations;
        
        if (filter.patientId() != null) {
            consultations = repository.findByPatientId(filter.patientId());
        } else if (filter.doctorId() != null) {
            consultations = repository.findByDoctorId(filter.doctorId());
        } else {
            consultations = repository.findAll();
        }
        
        if (filter.startDate() != null && filter.endDate() != null) {
            LocalDateTime start = LocalDateTime.parse(filter.startDate());
            LocalDateTime end = LocalDateTime.parse(filter.endDate());
            consultations = consultations.stream()
                .filter(c -> c.getDateTime().isAfter(start) && c.getDateTime().isBefore(end))
                .toList();
        }
        int fromIndex = 0;
        int toIndex = Math.min(filter.first(), consultations.size());
        List<ConsultationHistory> paginatedResults = consultations.subList(fromIndex, toIndex);
        
        boolean hasNext = consultations.size() > filter.first();
        boolean hasPrevious = false;
        
        return ConsultationConnectionDTO.from(
            paginatedResults, 
            hasNext, 
            hasPrevious, 
            paginatedResults.isEmpty() ? null : String.valueOf(paginatedResults.get(0).getId()),
            paginatedResults.isEmpty() ? null : String.valueOf(paginatedResults.get(paginatedResults.size() - 1).getId())
        );
    }

    @Transactional
    public ConsultationHistory update(Long id, UpdateConsultationInputDTO input) {
        ConsultationHistory consultation = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        
        if (input.dateTime() != null) {
            consultation.setDateTime(LocalDateTime.parse(input.dateTime()));
        }
        if (input.description() != null) {
            consultation.setDescription(input.description());
        }
        if (input.notes() != null) {
            consultation.setNotes(input.notes());
        }
        
        return repository.save(consultation);
    }

    @Transactional
    public Boolean cancel(Long id, String reason) {
        try {
            ConsultationHistory consultation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
            
            repository.delete(consultation);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public ConsultationHistory reschedule(Long id, LocalDateTime newDateTime) {
        ConsultationHistory consultation = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
        
        consultation.setDateTime(newDateTime);
        return repository.save(consultation);
    }

    @Transactional(readOnly = true)
    public List<TimeSlotDTO> findAvailableSlots(Long doctorId, String date) {
        LocalDate targetDate = LocalDate.parse(date);
        List<TimeSlotDTO> slots = new ArrayList<>();
        
        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
        
        List<ConsultationHistory> existingConsultations = repository.findByDoctorIdAndDateTimeBetween(
            doctorId, startOfDay, endOfDay);
        
        Set<LocalTime> occupiedTimes = existingConsultations.stream()
            .map(c -> c.getDateTime().toLocalTime())
            .collect(Collectors.toSet());
        
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(18, 0);
        
        LocalTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            LocalDateTime slotDateTime = LocalDateTime.of(targetDate, currentTime);
            
            boolean isOccupied = occupiedTimes.contains(currentTime);
            
            if (isOccupied) {
                slots.add(TimeSlotDTO.unavailable(slotDateTime, 30));
            } else {
                slots.add(TimeSlotDTO.available(slotDateTime, 30));
            }
            
            currentTime = currentTime.plusMinutes(30);
        }
        
        return slots;
    }
}
