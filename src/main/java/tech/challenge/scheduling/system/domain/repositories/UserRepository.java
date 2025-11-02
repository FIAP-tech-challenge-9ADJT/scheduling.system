package tech.challenge.scheduling.system.domain.repositories;

import tech.challenge.scheduling.system.domain.entities.User;
import tech.challenge.scheduling.system.domain.valueobjects.Email;
import tech.challenge.scheduling.system.domain.valueobjects.Login;
import tech.challenge.scheduling.system.domain.valueobjects.UserId;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(UserId id);

    Optional<User> findByLogin(Login login);

    User save(User user);

    void delete(UserId id);

    boolean existsByEmail(Email email);

    boolean existsByLogin(Login login);

    boolean existsById(UserId id);
}