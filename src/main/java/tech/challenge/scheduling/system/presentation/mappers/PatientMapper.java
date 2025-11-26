package tech.challenge.scheduling.system.presentation.mappers;

import tech.challenge.scheduling.system.domain.entities.Patient;
import tech.challenge.scheduling.system.presentation.controllers.PatientController.PatientRequest;

public class PatientMapper {
    public static Patient toEntity(PatientRequest request) {
        return Patient.create(
            request.name(), request.email(), request.login(), request.password(),
            request.cpf(), request.address(), request.addressNumber(),
            request.city(), request.state(), request.postalCode()
        );
    }
}
