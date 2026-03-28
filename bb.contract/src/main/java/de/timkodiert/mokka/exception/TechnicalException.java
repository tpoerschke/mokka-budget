package de.timkodiert.mokka.exception;

import lombok.Getter;
import org.jspecify.annotations.Nullable;

@Getter
public class TechnicalException extends RuntimeException {

    public enum Reason {
        PROGRAMMING_ERROR, FXML_NOT_FOUND
    }

    private final Reason reason;

    private TechnicalException(Reason reason, String message, @Nullable Throwable cause) {
        super(message, cause);
        this.reason = reason;
    }

    public static TechnicalException forProgrammingError(String message, Throwable cause) {
        return new TechnicalException(Reason.PROGRAMMING_ERROR, message, cause);
    }

    public static TechnicalException forProgrammingError(Throwable cause) {
        return new TechnicalException(Reason.PROGRAMMING_ERROR, cause.getMessage(), cause);
    }

    public static TechnicalException forFxmlNotFound(Throwable cause) {
        return new TechnicalException(Reason.FXML_NOT_FOUND, cause.getMessage(), cause);
    }
}
