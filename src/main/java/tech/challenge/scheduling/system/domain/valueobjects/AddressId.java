package tech.challenge.scheduling.system.domain.valueobjects;

import java.util.Objects;

public record AddressId(Long value) {

    public AddressId {
        Objects.requireNonNull(value, "Address ID cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("Address ID must be positive");
        }
    }

    public static AddressId of(Long value) {
        return new AddressId(value);
    }
}