package tech.challenge.scheduling.system.presentation.dtos.graphql;

import tech.challenge.scheduling.system.domain.entities.ConsultationHistory;
import java.util.List;

public record ConsultationConnectionDTO(
    List<ConsultationEdgeDTO> edges,
    PageInfoDTO pageInfo,
    Integer totalCount
) {
    public static ConsultationConnectionDTO from(List<ConsultationHistory> consultations, boolean hasNext, boolean hasPrevious, String startCursor, String endCursor) {
        List<ConsultationEdgeDTO> edges = consultations.stream()
            .map(consultation -> new ConsultationEdgeDTO(
                consultation,
                String.valueOf(consultation.getId())
            ))
            .toList();
            
        PageInfoDTO pageInfo = new PageInfoDTO(hasNext, hasPrevious, startCursor, endCursor);
        
        return new ConsultationConnectionDTO(edges, pageInfo, consultations.size());
    }
}
