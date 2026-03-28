package de.timkodiert.mokka.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;

@Getter
@Setter
public class UniqueTurnoverDTO implements HasRowType {

    private int id;
    @NotBlank(message = "{uniqueTurnover.biller.notBlank}")
    private String biller;
    @NotNull(message = "{attribute.notNull}")
    private LocalDate date;
    private String note;
    private String receiptImagePath;
    private List<UniqueTurnoverInformationDTO> paymentInformations = new ArrayList<>();
    private Reference<FixedTurnoverDTO> fixedTurnover;
    private AccountTurnoverDTO accountTurnover;

    public double getTotalValue() {
        return paymentInformations.stream().mapToDouble(UniqueTurnoverInformationDTO::getValueSigned).sum();
    }

    @Override
    public RowType getRowType() {
        return RowType.UNIQUE_EXPENSE;
    }
}
