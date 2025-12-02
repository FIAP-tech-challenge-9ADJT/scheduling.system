package tech.challenge.scheduling.system.presentation.dtos.graphql;

import java.time.LocalDateTime;

public record TimeSlotDTO(
    String dateTime,
    Boolean available,
    Integer duration
) {
    public static TimeSlotDTO available(LocalDateTime dateTime, int duration) {
        return new TimeSlotDTO(dateTime.toString(), true, duration);
    }
    
    public static TimeSlotDTO unavailable(LocalDateTime dateTime, int duration) {
        return new TimeSlotDTO(dateTime.toString(), false, duration);
    }
}
