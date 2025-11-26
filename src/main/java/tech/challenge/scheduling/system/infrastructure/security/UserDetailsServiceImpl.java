package tech.challenge.scheduling.system.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tech.challenge.scheduling.system.infrastructure.persistence.repositories.AdminJpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.DoctorJpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.NurseJpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.PatientJpaRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {


    private final AdminJpaRepository adminRepo;
    private final DoctorJpaRepository doctorRepo;
    private final NurseJpaRepository nurseRepo;
    private final PatientJpaRepository patientRepo;

    public UserDetailsServiceImpl(AdminJpaRepository adminRepo, DoctorJpaRepository doctorRepo, NurseJpaRepository nurseRepo, PatientJpaRepository patientRepo) {
        this.adminRepo = adminRepo;
        this.doctorRepo = doctorRepo;
        this.nurseRepo = nurseRepo;
        this.patientRepo = patientRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        var admin = adminRepo.findByLogin(login);
        if (admin != null) {
            return new MultiProfileUserDetails(admin.getId(), admin.getLogin(), admin.getPassword(), admin.getRole());
        }
        var doctor = doctorRepo.findByLogin(login);
        if (doctor != null) {
            return new MultiProfileUserDetails(doctor.getId(), doctor.getLogin(), doctor.getPassword(), doctor.getRole());
        }
        var nurse = nurseRepo.findByLogin(login);
        if (nurse != null) {
            return new MultiProfileUserDetails(nurse.getId(), nurse.getLogin(), nurse.getPassword(), nurse.getRole());
        }
        var patient = patientRepo.findByLogin(login);
        if (patient != null) {
            return new MultiProfileUserDetails(patient.getId(), patient.getLogin(), patient.getPassword(), patient.getRole());
        }
        throw new UsernameNotFoundException("Usuário não encontrado: " + login);
    }
}