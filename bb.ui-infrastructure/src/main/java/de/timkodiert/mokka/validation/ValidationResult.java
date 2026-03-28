package de.timkodiert.mokka.validation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {

    enum ResultType {
        VALID, ERROR;
    }

    @Getter
    private final ResultType type;
    @Getter
    private final @Nullable String message;

    public static ValidationResult valid() {
        return new ValidationResult(ResultType.VALID, null);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(ResultType.ERROR, message);
    }
}
