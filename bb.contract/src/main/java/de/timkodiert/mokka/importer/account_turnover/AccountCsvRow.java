package de.timkodiert.mokka.importer.account_turnover;

import java.time.LocalDate;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountCsvRow {

    private LocalDate date;
    private String receiver;
    private String postingText;
    private String reference;
    private int amount;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        AccountCsvRow that = (AccountCsvRow) other;
        return date.equals(that.getDate())
                && receiver.equals(that.getReceiver())
                && reference.equals(that.getReference())
                && amount == that.getAmount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, receiver, reference, amount);
    }
}
