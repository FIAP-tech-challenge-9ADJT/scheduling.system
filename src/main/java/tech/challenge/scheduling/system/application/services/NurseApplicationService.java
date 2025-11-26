package tech.challenge.scheduling.system.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.NurseJpaEntity;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.NurseJpaRepository;
import java.time.LocalDateTime;

@Service
public class NurseApplicationService {
    private final NurseJpaRepository nurseJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public NurseApplicationService(NurseJpaRepository nurseJpaRepository, PasswordEncoder passwordEncoder) {
        this.nurseJpaRepository = nurseJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public NurseJpaEntity createNurse(Long id, String name, String email, String login, String password, String coren) {
        NurseJpaEntity nurse = new NurseJpaEntity();
        nurse.setId(id);
        nurse.setName(name);
        nurse.setEmail(email);
        nurse.setLogin(login);
        nurse.setPassword(passwordEncoder.encode(password));
        nurse.setCoren(coren);
        nurse.setRole("NURSE");
        nurse.setCreatedAt(LocalDateTime.now());
        nurse.setUpdatedAt(LocalDateTime.now());
        return nurseJpaRepository.save(nurse);
    }

    @Transactional(readOnly = true)
    public NurseJpaEntity getNurseById(Long id) {
        return nurseJpaRepository.findById(id).orElse(null);
    }

    @Transactional
    public NurseJpaEntity updateNurse(Long id, String name, String email, String login, String password, String coren) {
        NurseJpaEntity existing = getNurseById(id);
        if (existing == null) return null;
        existing.setName(name);
        existing.setEmail(email);
        existing.setLogin(login);
        existing.setPassword(passwordEncoder.encode(password));
        existing.setCoren(coren);
        existing.setUpdatedAt(LocalDateTime.now());
        return nurseJpaRepository.save(existing);
    }

    @Transactional
    public boolean deleteNurse(Long id) {
        NurseJpaEntity existing = getNurseById(id);
        if (existing == null) return false;
        nurseJpaRepository.deleteById(id);
        return true;
    }
}
