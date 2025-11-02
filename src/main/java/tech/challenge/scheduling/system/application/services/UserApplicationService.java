package tech.challenge.scheduling.system.application.services;

import org.springframework.stereotype.Service;
import tech.challenge.scheduling.system.application.usecases.CreateUserUseCaseImpl;
import tech.challenge.scheduling.system.domain.entities.User;
import tech.challenge.scheduling.system.domain.usecases.user.FindUserUseCase;
import tech.challenge.scheduling.system.domain.usecases.user.UpdateUserUseCase;
import tech.challenge.scheduling.system.domain.valueobjects.UserId;
import tech.challenge.scheduling.system.domain.entities.Role;

@Service
public class UserApplicationService {

    private final CreateUserUseCaseImpl createUserUseCase;
    private final FindUserUseCase findUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;

    public UserApplicationService(CreateUserUseCaseImpl createUserUseCase,
            FindUserUseCase findUserUseCase,
            UpdateUserUseCase updateUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.findUserUseCase = findUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
    }

    public User createPatient(String name, String email, String login, String password) {
        return createUserUseCase.execute(name, email, login, password, Role.RoleName.PATIENT);
    }

    public User createNurse(String name, String email, String login, String password) {
        return createUserUseCase.execute(name, email, login, password, Role.RoleName.NURSE);
    }

    public User createDoctor(String name, String email, String login, String password) {
        return createUserUseCase.execute(name, email, login, password, Role.RoleName.DOCTOR);
    }

    public User findUser(UserId userId) {
        return findUserUseCase.execute(userId);
    }

    public User updateUser(UserId userId, String name, String email) {
        return updateUserUseCase.execute(userId, name, email);
    }
}