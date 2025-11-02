package tech.challenge.scheduling.system.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import tech.challenge.scheduling.system.validations.validators.ValidCPFValidator;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidCPFValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCPF {
    String message() default "CPF inválido";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}