package de.timkodiert.mokka.importer.account_turnover;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.timkodiert.mokka.domain.model.ImportConfiguration;
import de.timkodiert.mokka.exception.TechnicalException;

public class FileParser extends ImportStep {

    private static final Logger LOG = LoggerFactory.getLogger(FileParser.class);

    private static final String LABEL_IO_ERROR = "turnoverImport.error.io";
    private static final String LABEL_NO_CSV_CONTENT = "turnoverImport.error.noCsvContent";
    private static final String LABEL_MISSING_COLUMNS = "turnoverImport.error.missingColumns";
    private static final String LABEL_PARSE_EXCEPTION_DATE_OR_AMOUNT = "turnoverImport.error.parseExceptionDateOrAmount";

    private static final char CSV_DELIMITER = ';';
    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    protected void doHandle(ImportResult result, ImportContext context) {
        ImportConfiguration config = context.configuration();
        String csvContent = null;
        try (var reader = Files.newBufferedReader(context.filePath(), config.getEncoding().getCharset())) {
            csvContent = reader.lines().skip(config.getSkipLines()).reduce(this::concatLines).orElse(null);
        } catch (IOException e) {
            LOG.error("CSV file could not be read!", e);
            result.setErrorStatus(LABEL_IO_ERROR);
            return;
        }

        if (csvContent == null) {
            LOG.error("No CSV content found.");
            result.setErrorStatus(LABEL_NO_CSV_CONTENT);
            return;
        }

        CSVFormat format = CSVFormat.DEFAULT.builder().setDelimiter(CSV_DELIMITER).get();
        try (var parser = CSVParser.parse(csvContent, format)) {
            List<ImportInformation> iiList = parser.getRecords()
                                                   .stream()
                                                   .map(r -> mapToAccountCsvRow(r, config, result))
                                                   .filter(Objects::nonNull)
                                                   .map(ImportInformation::from)
                                                   .toList();
            result.getImportInformationList().addAll(iiList);
        } catch (IOException e) {
            throw TechnicalException.forProgrammingError(e);
        }
    }

    private @Nullable AccountCsvRow mapToAccountCsvRow(CSVRecord csvRecord, ImportConfiguration config, ImportResult result) {
        if (result.getStatus() == ImportStatus.ERROR) {
            return null;
        }
        if (!csvRecord.isSet(config.getMaxColumnIndex())) {
            result.setErrorStatus(LABEL_MISSING_COLUMNS);
            return null;
        }
        String dateStr = csvRecord.get(config.getColumnDate() - 1);
        String receiver = csvRecord.get(config.getColumnReceiver() - 1);
        String postingText = csvRecord.get(config.getColumnPostingText() - 1);
        String reference = csvRecord.get(config.getColumnReference() - 1);
        String amountStr = csvRecord.get(config.getColumnAmount() - 1);

        try {
            LocalDate date = LocalDate.parse(dateStr, LOCAL_DATE_FORMATTER);
            int amount = CsvAmountToIntegerConverter.convert(amountStr);
            return new AccountCsvRow(date, receiver, postingText, reference, amount);
        } catch (DateTimeParseException | ImportFormatException e) {
            result.setErrorStatus(LABEL_PARSE_EXCEPTION_DATE_OR_AMOUNT);
        }
        return null;
    }

    private String concatLines(String line1, String line2) {
        return line1 + "\n" + line2;
    }
}
