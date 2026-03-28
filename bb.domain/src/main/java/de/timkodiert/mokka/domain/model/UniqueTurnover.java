package de.timkodiert.mokka.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
public class UniqueTurnover extends BaseEntity {

    @Setter
    @NotBlank
    private String biller;

    @Setter
    @NotNull
    private LocalDate date;

    @Setter
    private String note;

    @Setter
    private String receiptImagePath;

    @Setter
    @Size(min = 1)
    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UniqueTurnoverInformation> paymentInformations = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "uniqueExpense", cascade = CascadeType.ALL, orphanRemoval = true)
    private AccountTurnover accountTurnover;

    @Setter
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "fixed_turnover_id")
    private FixedTurnover fixedTurnover;

    public int getTotalValue() {
        return paymentInformations.stream().mapToInt(UniqueTurnoverInformation::getValueSigned).sum();
    }

    public boolean hasImport() {
        return accountTurnover != null;
    }
}
