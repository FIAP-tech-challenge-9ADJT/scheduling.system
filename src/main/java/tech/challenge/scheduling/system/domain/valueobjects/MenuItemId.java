package tech.challenge.scheduling.system.domain.valueobjects;

import java.util.Objects;

public record MenuItemId(Long value) {

    public MenuItemId {
        Objects.requireNonNull(value, "MenuItem ID cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("MenuItem ID must be positive");
        }
    }

    public static MenuItemId of(Long value) {
        return new MenuItemId(value);
    }
}
