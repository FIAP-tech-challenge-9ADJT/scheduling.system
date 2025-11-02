package tech.challenge.scheduling.system.validations.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.UserJpaRepository;
import tech.challenge.scheduling.system.validations.UniqueLogin;

@Component
public class UniqueLoginValidator implements ConstraintValidator<UniqueLogin, String> {

    private final UserJpaRepository userRepository;

    public UniqueLoginValidator(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        if (login == null || login.trim().isEmpty()) {
            return true; // Deixa a validação @NotBlank cuidar disso
        }
        return !userRepository.existsByLogin(login);
    }
}