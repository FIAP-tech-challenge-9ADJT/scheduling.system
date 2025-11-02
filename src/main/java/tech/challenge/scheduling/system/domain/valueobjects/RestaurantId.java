package tech.challenge.scheduling.system.domain.valueobjects;

import java.util.Objects;

public record RestaurantId(Long value) {

    public RestaurantId {
        Objects.requireNonNull(value, "Restaurant ID cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Restaurant ID must be positive");
        }
    }

    public static RestaurantId of(Long value) {
        return new RestaurantId(value);
    }
}
