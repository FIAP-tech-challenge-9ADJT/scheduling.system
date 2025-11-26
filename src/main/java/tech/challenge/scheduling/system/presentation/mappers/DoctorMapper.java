package tech.challenge.scheduling.system.presentation.mappers;

import tech.challenge.scheduling.system.domain.entities.Doctor;
import tech.challenge.scheduling.system.presentation.controllers.DoctorController.DoctorRequest;

public class DoctorMapper {
    public static Doctor toEntity(DoctorRequest request) {
        return Doctor.create(
            request.name(), request.email(), request.login(), request.password(), request.crm()
        );
    }
}
