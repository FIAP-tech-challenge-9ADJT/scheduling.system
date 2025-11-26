package tech.challenge.scheduling.system.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.challenge.scheduling.system.infrastructure.persistence.entities.AdminJpaEntity;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.AdminJpaRepository;
import java.time.LocalDateTime;

@Service
public class AdministratorApplicationService {
    private final AdminJpaRepository adminJpaRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdministratorApplicationService(AdminJpaRepository adminJpaRepository, PasswordEncoder passwordEncoder) {
        this.adminJpaRepository = adminJpaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AdminJpaEntity createAdmin(Long id, String name, String email, String login, String password) {
        AdminJpaEntity admin = new AdminJpaEntity();
        admin.setId(id);
        admin.setName(name);
        admin.setEmail(email);
        admin.setLogin(login);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setRole("ADMIN");
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        return adminJpaRepository.save(admin);
    }

    @Transactional(readOnly = true)
    public AdminJpaEntity getAdminById(Long id) {
        return adminJpaRepository.findById(id).orElse(null);
    }

    @Transactional
    public AdminJpaEntity updateAdmin(Long id, String name, String email, String login, String password) {
        AdminJpaEntity existing = getAdminById(id);
        if (existing == null) return null;
        existing.setName(name);
        existing.setEmail(email);
        existing.setLogin(login);
        existing.setPassword(passwordEncoder.encode(password));
        existing.setUpdatedAt(LocalDateTime.now());
        return adminJpaRepository.save(existing);
    }

    @Transactional
    public boolean deleteAdmin(Long id) {
        AdminJpaEntity existing = getAdminById(id);
        if (existing == null) return false;
        adminJpaRepository.deleteById(id);
        return true;
    }
}
