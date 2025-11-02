package tech.challenge.scheduling.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.UserJpaRepository;
import tech.challenge.scheduling.system.validations.UniqueEmail;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final UserJpaRepository userRepository;

    public UniqueEmailValidator(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Deixa a validação @NotBlank cuidar disso
        }
        return !userRepository.existsByEmail(email);
    }
}