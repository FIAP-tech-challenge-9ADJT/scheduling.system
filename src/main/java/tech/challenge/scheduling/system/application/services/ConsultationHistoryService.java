package tech.challenge.scheduling.system.application.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.ConsultationHistoryRepository;

import java.util.List;
import java.util.Optional;

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

}
