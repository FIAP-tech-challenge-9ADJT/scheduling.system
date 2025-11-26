package tech.challenge.scheduling.system.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.challenge.scheduling.system.domain.entities.Role;
import tech.challenge.scheduling.system.domain.entities.User;
import java.util.Comparator;
import tech.challenge.scheduling.system.domain.exceptions.UserAlreadyExistsException;
import tech.challenge.scheduling.system.domain.repositories.RoleRepository;
import tech.challenge.scheduling.system.domain.repositories.UserRepository;
import tech.challenge.scheduling.system.infrastructure.persistence.repositories.UserJpaRepository;
import tech.challenge.scheduling.system.domain.usecases.user.CreateUserUseCase;
import tech.challenge.scheduling.system.domain.valueobjects.Email;
import tech.challenge.scheduling.system.domain.valueobjects.Login;

@Service
public class CreateUserUseCaseImpl extends CreateUserUseCase {

    private final PasswordEncoder passwordEncoder;

    private final UserJpaRepository userJpaRepository;

    public CreateUserUseCaseImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserJpaRepository userJpaRepository) {
        super(userRepository, roleRepository);
        this.passwordEncoder = passwordEncoder;
        this.userJpaRepository = userJpaRepository;
    }

    public User execute(String name, String email, String login, String password,
            Role.RoleName roleName) {
        if (super.userRepository.existsByEmail(Email.of(email))) {
            throw new UserAlreadyExistsException("email", email);
        }
        if (super.userRepository.existsByLogin(Login.of(login))) {
            throw new UserAlreadyExistsException("login", login);
        }

        String encodedPassword = passwordEncoder.encode(password);

        Long nextId = userJpaRepository.findAll().stream()
            .map(u -> u.getId() != null ? u.getId() : 0L)
            .max(Comparator.naturalOrder())
            .orElse(0L) + 1;

        User user = User.createWithId(nextId, name, email, login, encodedPassword);

        Role role = super.roleRepository.findByName(roleName)
                .orElseThrow(
                        () -> new RuntimeException("Role '" + roleName + "' not found in DB. Check RoleRepository."));
        user = user.addRole(role);
        return super.userRepository.save(user);
    }
}