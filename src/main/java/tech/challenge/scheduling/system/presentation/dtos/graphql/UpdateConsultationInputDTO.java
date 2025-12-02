package tech.challenge.scheduling.system.presentation.dtos.graphql;

public record UpdateConsultationInputDTO(
    String dateTime,
    String description,
    String notes,
    String status
) {}
