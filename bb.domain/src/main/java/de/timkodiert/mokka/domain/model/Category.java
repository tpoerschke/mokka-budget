package de.timkodiert.mokka.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import de.timkodiert.mokka.budget.BudgetType;

@Getter
@NoArgsConstructor
@Entity
public class Category extends BaseEntity {

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private String description;

    @Setter
    @ManyToOne
    @JoinColumn(name = "group_id")
    private CategoryGroup group;

    @Setter
    private Integer budgetValue;

    @Setter
    @Column(nullable = false)
    private boolean budgetActive = true;

    @Setter
    @Enumerated(EnumType.STRING)
    private BudgetType budgetType;

    @OneToMany(mappedBy = "category")
    private List<FixedTurnover> fixedExpenses = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<UniqueTurnoverInformation> uniqueExpenseInformation = new ArrayList<>();

    public boolean hasActiveBudget() {
        return budgetActive && budgetValue != null;
    }

    public double getBudgetValueInEuro() {
        return budgetValue / 100.0;
    }

    public List<UniqueTurnoverInformation> getUniqueTurnoverInformation() {
        return uniqueExpenseInformation;
    }

    public List<UniqueTurnoverInformation> getUniqueTurnoverInformation(MonthYear monthYear) {
        return uniqueExpenseInformation.stream()
                                       .filter(uti -> uti.getExpense().getFixedTurnover() == null)
                                       .filter(uti -> monthYear.containsDate(uti.getExpense().getDate()))
                                       .toList();
    }

    public int sumTurnovers(MonthYear monthYear) {
        return switch (budgetType) {
            case MONTHLY -> sumTurnoversForMonth(monthYear);
            case ANNUAL -> sumAnnualBudget(monthYear.getYear());
        };
    }

    public int sumTurnoversForMonth(MonthYear monthYear) {
        int sum = 0;
        sum += fixedExpenses.stream().mapToInt(ft -> ft.getValueFor(monthYear)).sum();
        sum += getUniqueTurnoverInformation(monthYear).stream().mapToInt(UniqueTurnoverInformation::getValueSigned).sum();
        return sum;
    }

    private int sumAnnualBudget(int year) {
        int sum = 0;
        sum += fixedExpenses.stream().mapToInt(ft -> ft.getValueForYear(year)).sum();
        sum += uniqueExpenseInformation.stream()
                                       .filter(uti -> uti.getExpense().getFixedTurnover() == null)
                                       .filter(uti -> uti.getExpense().getDate().getYear() == year)
                                       .mapToInt(UniqueTurnoverInformation::getValueSigned)
                                       .sum();
        return sum;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
