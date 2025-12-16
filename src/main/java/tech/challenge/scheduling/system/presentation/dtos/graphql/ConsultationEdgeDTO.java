package tech.challenge.scheduling.system.presentation.dtos.graphql;

import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;

public record ConsultationEdgeDTO(
    ConsultationHistory node,
    String cursor
) {}
