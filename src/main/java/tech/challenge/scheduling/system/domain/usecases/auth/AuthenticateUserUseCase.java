package tech.challenge.scheduling.system.domain.usecases.auth;

import tech.challenge.scheduling.system.domain.entities.User;
import tech.challenge.scheduling.system.domain.exceptions.InvalidCredentialsException;
import tech.challenge.scheduling.system.domain.repositories.UserRepository;
import tech.challenge.scheduling.system.domain.valueobjects.Login;

public class AuthenticateUserUseCase {

    private final UserRepository userRepository;

    public AuthenticateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String login, String password) {
        User user = userRepository.findByLogin(Login.of(login))
                .orElseThrow(() -> new InvalidCredentialsException());

        // A validação da senha será feita na camada de infraestrutura
        // pois depende do PasswordEncoder do Spring Security

        return user;
    }
}