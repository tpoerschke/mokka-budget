package de.timkodiert.mokka.importer;

import java.time.LocalDate;
import java.util.Objects;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import com.opencsv.bean.CsvDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Nutzt die @CsvBindByPosition wegen Encoding-Problemen (s. Auftraggeber/Empfänger)
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccountCsvRow {

    public static final int SKIP_LINES = 14;

    @CsvBindByPosition(position = 1)
    @CsvDate("dd.MM.yyyy")
    private LocalDate date;

    @CsvBindByPosition(position = 2)
    private String receiver;

    @CsvBindByPosition(position = 3)
    private String postingText;

    @CsvBindByPosition(position = 4)
    private String reference;

    @CsvCustomBindByPosition(position = 7, converter = CsvAmountToIntegerConverter.class)
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
