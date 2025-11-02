package tech.challenge.scheduling.system.domain.valueobjects;

import java.util.Objects;
import java.util.regex.Pattern;

public record PostalCode(String value) {

    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");

    public PostalCode {
        Objects.requireNonNull(value, "Postal code cannot be null");
        String cleanValue = value.replaceAll("[^\\d]", "");
        if (!CEP_PATTERN.matcher(value).matches() && cleanValue.length() != 8) {
            throw new IllegalArgumentException("Invalid postal code format: " + value);
        }
    }

    public static PostalCode of(String value) {
        return new PostalCode(value);
    }

    public String getCleanValue() {
        return value.replaceAll("[^\\d]", "");
    }
}