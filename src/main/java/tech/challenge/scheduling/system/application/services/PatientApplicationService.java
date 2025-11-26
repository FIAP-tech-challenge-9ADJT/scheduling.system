package tech.challenge.scheduling.system.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.PatientJpaEntity;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.PatientJpaRepository;
import java.time.LocalDateTime;

@Service
public class PatientApplicationService {
    private final PatientJpaRepository patientJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PatientApplicationService(PatientJpaRepository patientJpaRepository, PasswordEncoder passwordEncoder) {
        this.patientJpaRepository = patientJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PatientJpaEntity createPatient(Long id, String name, String email, String login, String password,
                                String cpf, String address, String addressNumber, String city, String state, String postalCode) {
        PatientJpaEntity patient = new PatientJpaEntity();
        patient.setId(id);
        patient.setName(name);
        patient.setEmail(email);
        patient.setLogin(login);
        patient.setPassword(passwordEncoder.encode(password));
        patient.setCpf(cpf);
        patient.setAddress(address);
        patient.setAddressNumber(addressNumber);
        patient.setCity(city);
        patient.setState(state);
        patient.setPostalCode(postalCode);
        patient.setRole("PATIENT");
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());
        return patientJpaRepository.save(patient);
    }

    @Transactional(readOnly = true)
    public PatientJpaEntity getPatientById(Long id) {
        return patientJpaRepository.findById(id).orElse(null);
    }

    @Transactional
    public PatientJpaEntity updatePatient(Long id, String name, String email, String login, String password,
                                String cpf, String address, String addressNumber, String city, String state, String postalCode) {
        PatientJpaEntity existing = getPatientById(id);
        if (existing == null) return null;
        existing.setName(name);
        existing.setEmail(email);
        existing.setLogin(login);
        existing.setPassword(passwordEncoder.encode(password));
        existing.setCpf(cpf);
        existing.setAddress(address);
        existing.setAddressNumber(addressNumber);
        existing.setCity(city);
        existing.setState(state);
        existing.setPostalCode(postalCode);
        existing.setUpdatedAt(LocalDateTime.now());
        return patientJpaRepository.save(existing);
    }

    @Transactional
    public boolean deletePatient(Long id) {
        PatientJpaEntity existing = getPatientById(id);
        if (existing == null) return false;
        patientJpaRepository.deleteById(id);
        return true;
    }
}
