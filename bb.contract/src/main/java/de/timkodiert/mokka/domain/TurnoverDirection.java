package de.timkodiert.mokka.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TurnoverDirection {

    IN(1), OUT(-1);

    private final int sign;

    public static TurnoverDirection valueOf(double value) {
        return switch ((int) Math.signum(value)) {
            case 1 -> IN;
            case -1, 0 -> OUT;
            default -> throw new IllegalStateException("Unexpected value: " + (int) Math.signum(value));
        };
    }
}
