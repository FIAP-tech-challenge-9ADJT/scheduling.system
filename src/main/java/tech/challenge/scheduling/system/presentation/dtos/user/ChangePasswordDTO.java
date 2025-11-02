package tech.challenge.scheduling.system.presentation.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tech.challenge.scheduling.system.validations.PasswordMatches;

@PasswordMatches(password = "newPassword", confirmPassword = "newPasswordConfirmation")
public record ChangePasswordDTO(
                @NotBlank(message = "A senha atual é obrigatória") String currentPassword,

                @NotBlank(message = "A nova senha é obrigatória") @Size(min = 6, message = "A nova senha deve ter pelo menos 6 caracteres") String newPassword,

                @NotBlank(message = "A confirmação da nova senha é obrigatória") String newPasswordConfirmation) {
}
