package tech.challenge.scheduling.system.presentation.dtos.graphql;

public record ConsultationFilterDTO(
    Long patientId,
    Long doctorId,
    Long nurseId,
    String startDate,
    String endDate,
    String status
) {
    public ConsultationFilterDTO withPatientId(Long patientId) {
        return new ConsultationFilterDTO(patientId, doctorId, nurseId, startDate, endDate, status);
    }
}
