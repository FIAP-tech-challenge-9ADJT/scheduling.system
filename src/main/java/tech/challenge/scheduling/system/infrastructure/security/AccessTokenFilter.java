package tech.challenge.scheduling.system.infrastructure.security;

import tech.challenge.scheduling.system.infrastructure.persistence.repositories.AdminJpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.DoctorJpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.NurseJpaRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.PatientJpaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class AccessTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final AdminJpaRepository adminRepo;
    private final DoctorJpaRepository doctorRepo;
    private final NurseJpaRepository nurseRepo;
    private final PatientJpaRepository patientRepo;

    private static final List<String> PUBLIC_URLS = List.of(
            "/v3/api-docs",
            "/v3/api-docs/",
            "/v3/api-docs/swagger-config",
            "/swagger-ui.html",
            "/swagger-ui/",
            "/swagger-ui/index.html",
            "/swagger-resources",
            "/swagger-resources/",
            "/webjars/");

    public AccessTokenFilter(TokenService tokenService, AdminJpaRepository adminRepo, DoctorJpaRepository doctorRepo, NurseJpaRepository nurseRepo, PatientJpaRepository patientRepo) {
        this.tokenService = tokenService;
        this.adminRepo = adminRepo;
        this.doctorRepo = doctorRepo;
        this.nurseRepo = nurseRepo;
        this.patientRepo = patientRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isPublicUrl(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoveryRequestToken(request);

        if (token != null) {
            try {
                String login = tokenService.verifyToken(token);

                MultiProfileUserDetails userDetails = null;
                var admin = adminRepo.findByLogin(login);
                if (admin != null) {
                    userDetails = new MultiProfileUserDetails(admin.getId(), admin.getLogin(), admin.getPassword(), admin.getRole());
                }
                var doctor = doctorRepo.findByLogin(login);
                if (doctor != null) {
                    userDetails = new MultiProfileUserDetails(doctor.getId(), doctor.getLogin(), doctor.getPassword(), doctor.getRole());
                }
                var nurse = nurseRepo.findByLogin(login);
                if (nurse != null) {
                    userDetails = new MultiProfileUserDetails(nurse.getId(), nurse.getLogin(), nurse.getPassword(), nurse.getRole());
                }
                var patient = patientRepo.findByLogin(login);
                if (patient != null) {
                    userDetails = new MultiProfileUserDetails(patient.getId(), patient.getLogin(), patient.getPassword(), patient.getRole());
                }
                if (userDetails == null) {
                    throw new RuntimeException("Usuário não encontrado");
                }
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicUrl(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }

    private String recoveryRequestToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}