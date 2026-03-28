package de.timkodiert.mokka.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.NonNull;

import de.timkodiert.mokka.importer.AccountCsvRow;
import de.timkodiert.mokka.importer.ImportInformation;

@Getter
@NoArgsConstructor
@Entity
public class AccountTurnover extends BaseEntity implements Comparable<AccountTurnover> {

    @Column(nullable = false)
    private LocalDate date;

    private String receiver;

    private String postingText;

    private String reference;

    @Column(nullable = false)
    private int amount;

    @Setter
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "unique_turnover_id", nullable = false)
    private UniqueTurnover uniqueExpense;

    public AccountTurnover(LocalDate date, String receiver, String postingText, String reference, int amount) {
        this.date = date;
        this.receiver = receiver;
        this.postingText = postingText;
        this.reference = reference;
        this.amount = amount;
    }

    public AccountTurnover(AccountCsvRow accountCsvRow) {
        this.date = accountCsvRow.getDate();
        this.receiver = accountCsvRow.getReceiver();
        this.postingText = accountCsvRow.getPostingText();
        this.reference = accountCsvRow.getReference();
        this.amount = accountCsvRow.getAmount();
    }

    public static AccountTurnover withFixedTurnover(ImportInformation importInformation, FixedTurnover fixedTurnover) {
        AccountTurnover accountTurnover = withUniqueTurnover(importInformation);
        accountTurnover.getUniqueExpense().setFixedTurnover(fixedTurnover);
        fixedTurnover.getUniqueTurnovers().add(accountTurnover.getUniqueExpense());
        return accountTurnover;
    }

    public static AccountTurnover withUniqueTurnover(ImportInformation importInformation) {
        UniqueTurnover ut = createUniqueTurnover(importInformation);
        AccountTurnover accountTurnover = new AccountTurnover(importInformation.getAccountCsvRow());
        accountTurnover.setUniqueExpense(ut);
        ut.setAccountTurnover(accountTurnover);
        return accountTurnover;
    }

    private static UniqueTurnover createUniqueTurnover(ImportInformation importInformation) {
        UniqueTurnover ut = new UniqueTurnover();
        ut.setBiller(importInformation.receiverProperty().get());
        ut.setDate(importInformation.getAccountCsvRow().getDate());
        // List.of() hier gewrappt, da die UnmodifiableList sonst "überlebt" und
        // der Umsatz nach dem Import nicht bearbeitet werden kann.
        ut.setPaymentInformations(new ArrayList<>(List.of(UniqueTurnoverInformation.total(ut, importInformation.getAccountCsvRow().getAmount()))));
        return ut;
    }

    public AccountCsvRow asAccountCsvRow() {
        return new AccountCsvRow(date, receiver, postingText, reference, amount);
    }

    @SuppressWarnings("java:S1210")
    @Override
    public int compareTo(@NonNull AccountTurnover o) {
        return date.compareTo(o.getDate());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        AccountTurnover that = (AccountTurnover) other;
        return date.equals(that.getDate())
                && receiver.equals(that.getReceiver())
                && reference.equals(that.getReference())
                && amount == that.getAmount();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, receiver, reference, amount);
    }
}
