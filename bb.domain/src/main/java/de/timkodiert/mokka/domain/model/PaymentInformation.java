package de.timkodiert.mokka.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import de.timkodiert.mokka.domain.PaymentType;

@Getter
@NoArgsConstructor
@Entity
public class PaymentInformation extends BaseEntity {

    @Setter
    private MonthYear start;
    @Setter
    private MonthYear end;

    @Setter
    @Enumerated(EnumType.STRING)
    private PaymentType type;
    @Column(nullable = false)
    private final List<Integer> monthsOfPayment = new ArrayList<>();
    @Setter
    private int value;

    @Setter
    @ManyToOne
    @JoinColumn(name = "turnover_id", nullable = false)
    private FixedTurnover expense;

    public PaymentInformation(FixedTurnover expense, int value, List<Integer> monthsOfPayment, PaymentType type, MonthYear start, MonthYear end) {
        this.expense = expense;
        this.type = type;
        this.monthsOfPayment.addAll(monthsOfPayment);
        this.value = value;
        this.start = start;
        this.end = end;
    }

    public void setId(int id) {
        super.id = id;
    }

    public int getValueFor(MonthYear monthYear) {
        return this.validFor(monthYear) && this.monthsOfPayment.contains(monthYear.getMonth()) ? this.value : 0;
    }

    public boolean validFor(MonthYear monthYear) {
        if (this.start != null && this.start.compareTo(monthYear) > 0) {
            return false;
        }
        return this.end == null || this.end.compareTo(monthYear) >= 0;
    }

    public void setMonthsOfPayment(List<Integer> monthsOfPayment) {
        this.monthsOfPayment.clear();
        this.monthsOfPayment.addAll(monthsOfPayment);
    }
}