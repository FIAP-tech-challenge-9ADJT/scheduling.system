package tech.challenge.scheduling.system.domain.valueobjects;

import java.util.Objects;

public record RestaurantAddressId(Long value) {

    public RestaurantAddressId {
        Objects.requireNonNull(value, "Address ID cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Address ID must be positive");
        }
    }

    public static RestaurantAddressId of(Long value) {
        return new RestaurantAddressId(value);
    }
}
