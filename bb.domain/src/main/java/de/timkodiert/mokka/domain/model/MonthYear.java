package de.timkodiert.mokka.domain.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@EqualsAndHashCode
@Getter
// TODO: Es gibt noch kein MonthYearTO, sollte aber eingeführt werden, damit "Client" und "Server" besser getrennt werden.
// Dann kann hier auch das Serializable entfernt werden.
public class MonthYear implements Comparable<MonthYear>, Serializable {

    @Column(nullable = true)
    private int month;
    @Column(nullable = true)
    private int year;

    public static MonthYear of(YearMonth yearMonth) {
        return new MonthYear(yearMonth.getMonthValue(), yearMonth.getYear());
    }

    public static List<MonthYear> range(MonthYear from, MonthYear to) {
        if (to.isBefore(from)) {
            return List.of();
        }
        ArrayList<MonthYear> monthYearList = new ArrayList<>();
        MonthYear current = MonthYear.of(from.getMonth(), from.getYear());
        while (!current.isAfter(to)) {
            monthYearList.add(current);
            current = current.plusMonths(1);
        }
        return monthYearList;
    }

    public YearMonth asYearMonth() {
        return YearMonth.of(year, month);
    }

    public boolean isAfter(MonthYear other) {
        if (other == null) {
            return true;
        }
        return this.compareTo(other) > 0;
    }

    public boolean isBefore(MonthYear other) {
        return this.compareTo(other) < 0;
    }

    @Override
    public int compareTo(MonthYear other) {
        if (this.equals(other)) {
            return 0;
        }

        // Kleiner
        if (this.getYear() < other.getYear()) {
            return -1;
        }
        if (this.getYear() == other.getYear() && this.getMonth() < other.getMonth()) {
            return -1;
        }

        // Größer
        if (this.getYear() > other.getYear()) {
            return 1;
        }
        if (this.getYear() == other.getYear() && this.getMonth() > other.getMonth()) {
            return 1;
        }

        // Um den Compiler glücklich zu machen
        return 0;
    }

    public MonthYear plusMonths(int count) {
        LocalDate monthYearDate = LocalDate.of(this.year, this.month, 1).plusMonths(count);
        return MonthYear.of(monthYearDate.getMonthValue(), monthYearDate.getYear());
    }

    public boolean containsDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return month == date.getMonthValue() && year == date.getYear();
    }

    public static MonthYear now() {
        LocalDate now = LocalDate.now();
        return new MonthYear(now.getMonthValue(), now.getYear());
    }

    @Override
    public String toString() {
        return this.month + "." + this.year;
    }
}
