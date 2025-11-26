package tech.challenge.scheduling.system.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.DoctorJpaEntity;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.DoctorJpaRepository;
import java.time.LocalDateTime;

@Service
public class DoctorApplicationService {
    private final DoctorJpaRepository doctorJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DoctorApplicationService(DoctorJpaRepository doctorJpaRepository, PasswordEncoder passwordEncoder) {
        this.doctorJpaRepository = doctorJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public DoctorJpaEntity createDoctor(Long id, String name, String email, String login, String password, String crm) {
        DoctorJpaEntity doctor = new DoctorJpaEntity();
        doctor.setId(id);
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setLogin(login);
        doctor.setPassword(passwordEncoder.encode(password));
        doctor.setCrm(crm);
        doctor.setRole("DOCTOR");
        doctor.setCreatedAt(LocalDateTime.now());
        doctor.setUpdatedAt(LocalDateTime.now());
        return doctorJpaRepository.save(doctor);
    }

    @Transactional(readOnly = true)
    public DoctorJpaEntity getDoctorById(Long id) {
        return doctorJpaRepository.findById(id).orElse(null);
    }

    @Transactional
    public DoctorJpaEntity updateDoctor(Long id, String name, String email, String login, String password, String crm) {
        DoctorJpaEntity existing = getDoctorById(id);
        if (existing == null) return null;
        existing.setName(name);
        existing.setEmail(email);
        existing.setLogin(login);
        existing.setPassword(passwordEncoder.encode(password));
        existing.setCrm(crm);
        existing.setUpdatedAt(LocalDateTime.now());
        return doctorJpaRepository.save(existing);
    }

    @Transactional
    public boolean deleteDoctor(Long id) {
        DoctorJpaEntity existing = getDoctorById(id);
        if (existing == null) return false;
        doctorJpaRepository.deleteById(id);
        return true;
    }
}
