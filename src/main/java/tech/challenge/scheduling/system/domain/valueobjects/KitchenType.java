package tech.challenge.scheduling.system.domain.valueobjects;

import java.util.Objects;
import java.util.Set;

public record KitchenType(String value) {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "Italian",
            "Japanese",
            "Brazilian",
            "Mexican",
            "Chinese",
            "French",
            "Indian");

    public KitchenType {
        Objects.requireNonNull(value, "Cuisine type cannot be null");
        if (value.trim().isEmpty()) {
            throw new IllegalArgumentException("Cuisine type cannot be empty");
        }
        if (!ALLOWED_TYPES.contains(value.trim())) {
            throw new IllegalArgumentException("Invalid cuisine type: " + value.trim());
        }
    }

    public static KitchenType of(String value) {
        return new KitchenType(value.trim());
    }
}