package tech.challenge.scheduling.system.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import tech.challenge.scheduling.system.validations.validators.PasswordMatchesValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatches {
    String message() default "As senhas não coincidem";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String password();

    String confirmPassword();
}