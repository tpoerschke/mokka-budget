package de.timkodiert.mokka;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import com.opencsv.CSVWriterBuilder;
import com.opencsv.ICSVWriter;

import de.timkodiert.mokka.importer.AccountCsvRow;
import de.timkodiert.mokka.util.IntervalUtils;
import de.timkodiert.mokka.util.MoneyEssentials;

import static de.timkodiert.mokka.util.MoneyEssentials.ROUNDING_MODE;
import static de.timkodiert.mokka.util.MoneyEssentials.asBigDecimal;

public class AccountCsvFileGenerator {

    private static final Random RANDOM = new Random(42042);

    // Wocheneinkauf
    // 2x pro Woche
    // - erster ab 30 Euro
    // - zweiter ab 15 Euro
    // leicht steigend
    private static final int GROCERIES_FIRST_MEAN = 1200;
    private static final int GROCERIES_FIRST_SCALE = 600;
    private static int groceriesFirstLowerBound = 3000;

    private static final int GROCERIES_SECOND_MEAN = 800;
    private static final int GROCERIES_SECOND_SCALE = 400;
    private static int groceriesSecondLowerBound = 1500;

    // Tanken
    // 1x im Monat
    // zwischen 60 - 70 Euro
    // konstant
    private static final int REFUELING_LOWER_BOUND = 6000;
    private static final int REFUELING_SPAN = 1500;

    // Parken
    // 0x - 6x im Monat
    // zwischen 2 - 8 Euro
    // konstant
    private static final int PARKING_TIMES_SPAN = 6;
    private static final int PARKING_LOWER_BOUND = 200;
    private static final int PARKING_SPAN = 600;

    // Einzigartige Ausgaben, die zur Kategorie Unterhaltung zugeordnet werden sollen
    private static final List<CsvLine> ADDITIONAL_TURNOVER = List.of(
            new CsvLine(LocalDate.of(2025, 7, 12), "AMAZON DIGITAL GERMANY GMBH", "D00-1231230-1234567 Prime Video TVO D I00Y000CAA1AA0O0", -399),
            new CsvLine(LocalDate.of(2025, 8, 2), "VISA THALIA", generateReference(LocalDate.of(2025, 8, 2)), -1250),
            new CsvLine(LocalDate.of(2025, 8, 8), "VISA KINOTICKETS.ONLINE", generateReference(LocalDate.of(2025, 8, 8)), -1500)
    );

    public static void main(String[] args) throws IOException {
        YearMonth start = YearMonth.of(2024, 10);
        YearMonth end = YearMonth.of(2025, 8);

        List<CsvLine> lines = generateWeeklyGroceries(start, end);
        lines.addAll(generateRefueling(start, end));
        lines.addAll(generateParking(start, end));
        lines.addAll(ADDITIONAL_TURNOVER);

        lines.sort(Comparator.comparing(CsvLine::date).reversed());

        ICSVWriter writer = new CSVWriterBuilder(new FileWriter("_output.csv")).withSeparator(';').build();
        IntStream.range(0, AccountCsvRow.SKIP_LINES).forEach(i -> writer.writeNext(CsvLine.emptyWritableArray()));
        lines.stream().map(CsvLine::toWritableArray).forEach(writer::writeNext);
        writer.close();
    }

    private static List<CsvLine> generateWeeklyGroceries(YearMonth start, YearMonth end) {
        List<CsvLine> groceries = new ArrayList<>();
        LocalDate startDate = start.atDay(1);
        LocalDate endDate = end.atDay(20);
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {

            LocalDate monday = current.with(WeekFields.ISO.dayOfWeek(), 1);
            LocalDate friday = current.with(WeekFields.ISO.dayOfWeek(), 5);

            if (!monday.isBefore(startDate) && !monday.isAfter(endDate)) {
                int value = groceriesFirstLowerBound + (int) RANDOM.nextGaussian(GROCERIES_FIRST_MEAN, GROCERIES_FIRST_SCALE);
                groceries.add(new CsvLine(monday, "VISA ALDI NORD", generateReference(monday), value * -1));
                groceriesFirstLowerBound += 100;
            }
            if (!friday.isBefore(startDate) && !friday.isAfter(endDate)) {
                int value = groceriesSecondLowerBound + (int) RANDOM.nextGaussian(GROCERIES_SECOND_MEAN, GROCERIES_SECOND_SCALE);
                groceries.add(new CsvLine(friday, "VISA LIDL SAGT DANKE", generateReference(monday), value * -1));
                groceriesSecondLowerBound += 100;
            }

            // Weiter zur nächsten Woche
            current = current.plusWeeks(1).with(WeekFields.ISO.dayOfWeek(), 1);
        }
        return groceries;
    }

    private static List<CsvLine> generateRefueling(YearMonth start, YearMonth end) {
        List<CsvLine> refueling = new ArrayList<>();
        IntervalUtils.yearMonthRange(start, end).forEach(yearMonth -> {
            LocalDate date = yearMonth.atDay(4);
            int amount = REFUELING_LOWER_BOUND + RANDOM.nextInt(REFUELING_SPAN);
            refueling.add(new CsvLine(date, "VISA STAR TANKSTELLE", generateReference(date), amount * -1));
        });
        return refueling;
    }

    private static List<CsvLine> generateParking(YearMonth start, YearMonth end) {
        List<CsvLine> parking = new ArrayList<>();
        IntervalUtils.yearMonthRange(start, end).forEach(yearMonth -> {
            int parkingTimes = RANDOM.nextInt(PARKING_TIMES_SPAN);
            IntStream.range(0, parkingTimes).forEach(i -> {
                LocalDate date = yearMonth.atDay(RANDOM.nextInt(28) + 1);
                int amount = PARKING_LOWER_BOUND + RANDOM.nextInt(PARKING_SPAN);
                parking.add(new CsvLine(date, "VISA PARKHAUS P1", generateReference(date), amount * -1));
            });
        });
        return parking;
    }

    private static String generateReference(LocalDate date) {
        String dateStr = date.format(DateTimeFormatter.ofPattern("dd.MM"));
        return String.format("NR XXXX 1011 BOCHUM DE KAUFUMSATZ %s 00.00 123400 ARN6281XXX Apple Pay", dateStr);
    }

    record CsvLine(LocalDate date, String receiver, String reference, int amount) {

        public String[] toWritableArray() {
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String valueStr = new DecimalFormat("0.00").format(asBigDecimal(amount).divide(MoneyEssentials.FACTOR_100, ROUNDING_MODE));
            return new String[] {dateStr, dateStr, receiver, "Lastschrift", reference, "0,00", "EUR", valueStr, "EUR"};
        }

        public static String[] emptyWritableArray() {
            return new String[] {"", "", "", "", "", "", "", "", ""};
        }
    }
}
