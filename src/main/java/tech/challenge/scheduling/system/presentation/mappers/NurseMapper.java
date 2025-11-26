package tech.challenge.scheduling.system.presentation.mappers;

import tech.challenge.scheduling.system.domain.entities.Nurse;
import tech.challenge.scheduling.system.presentation.controllers.NurseController.NurseRequest;

public class NurseMapper {
    public static Nurse toEntity(NurseRequest request) {
        return Nurse.create(
            request.name(), request.email(), request.login(), request.password(), request.coren()
        );
    }
}
