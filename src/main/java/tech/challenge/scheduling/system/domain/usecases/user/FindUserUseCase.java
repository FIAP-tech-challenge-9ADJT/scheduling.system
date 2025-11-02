package tech.challenge.scheduling.system.domain.usecases.user;

import tech.challenge.scheduling.system.domain.entities.User;
import tech.challenge.scheduling.system.domain.exceptions.UserNotFoundException;
import tech.challenge.scheduling.system.domain.repositories.UserRepository;
import tech.challenge.scheduling.system.domain.valueobjects.UserId;

public class FindUserUseCase {

    private final UserRepository userRepository;

    public FindUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(UserId userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}