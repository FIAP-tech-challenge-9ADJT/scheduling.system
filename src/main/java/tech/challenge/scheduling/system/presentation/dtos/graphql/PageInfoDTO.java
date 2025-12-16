package tech.challenge.scheduling.system.presentation.dtos.graphql;

public record PageInfoDTO(
    Boolean hasNextPage,
    Boolean hasPreviousPage,
    String startCursor,
    String endCursor
) {}
