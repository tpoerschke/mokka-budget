package de.timkodiert.mokka.domain;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountTurnoverDTO {
    private LocalDate date;
    private String receiver;
    private String postingText;
    private String reference;
    private int amount;
}
