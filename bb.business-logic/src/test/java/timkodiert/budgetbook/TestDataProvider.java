package timkodiert.budgetbook;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import org.jspecify.annotations.Nullable;

import timkodiert.budgetbook.domain.PaymentType;
import timkodiert.budgetbook.domain.TurnoverDirection;
import timkodiert.budgetbook.domain.model.Category;
import timkodiert.budgetbook.domain.model.FixedTurnover;
import timkodiert.budgetbook.domain.model.MonthYear;
import timkodiert.budgetbook.domain.model.PaymentInformation;
import timkodiert.budgetbook.domain.model.UniqueTurnover;
import timkodiert.budgetbook.domain.model.UniqueTurnoverInformation;

public class TestDataProvider {

    private TestDataProvider() {}

    public static UniqueTurnover createUniqueTurnover(LocalDate date, String receiver, int amount) {
        UniqueTurnover turnover = new UniqueTurnover();
        turnover.setDate(date);
        turnover.setBiller(receiver);
        UniqueTurnoverInformation info = UniqueTurnoverInformation.total(turnover, amount);
        turnover.getPaymentInformations().add(info);
        return turnover;
    }

    public static UniqueTurnover createUniqueTurnoverWithCategory(LocalDate date, String receiver, int amount, Category category) {
        UniqueTurnover turnover = createUniqueTurnover(date, receiver, amount);
        UniqueTurnoverInformation info = turnover.getPaymentInformations().getFirst();
        info.setCategory(category);
        category.getUniqueTurnoverInformation().add(info);
        return turnover;
    }

    public static FixedTurnover createFixedTurnover(String position, int value, MonthYear start, @Nullable MonthYear end) {
        PaymentInformation info = new PaymentInformation();
        info.setType(PaymentType.MONTHLY);
        info.setMonthsOfPayment(Arrays.stream(Month.values()).map(Month::getValue).toList());
        info.setValue(Math.abs(value));
        info.setStart(start);
        info.setEnd(end);
        FixedTurnover turnover = new FixedTurnover();
        turnover.setPosition(position);
        turnover.setDirection(value > 0 ? TurnoverDirection.IN : TurnoverDirection.OUT);
        turnover.getPaymentInformations().add(info);
        return turnover;
    }

    public static FixedTurnover createFixedTurnoverWithCategory(String position, int value, MonthYear start, @Nullable MonthYear end, Category category) {
        FixedTurnover turnover = createFixedTurnover(position, value, start, end);
        turnover.setCategory(category);
        category.getFixedExpenses().add(turnover);
        return turnover;
    }
}
