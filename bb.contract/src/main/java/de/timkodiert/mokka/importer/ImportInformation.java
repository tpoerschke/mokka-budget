package de.timkodiert.mokka.importer;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import de.timkodiert.mokka.domain.FixedTurnoverDTO;
import de.timkodiert.mokka.domain.Reference;
import de.timkodiert.mokka.representation.HasRowType;
import de.timkodiert.mokka.representation.RowType;


// TODO: Sollte eigentlich ein reines Client-Objekt sein, ähnliches
//  gilt für die AccountCsvRow. Dann könnte JavaFX eine reine
//  Client-Abhängigkeit sein und OpenCSV nur im Backend.
public class ImportInformation implements HasRowType {

    private static final String ANNOTATION_EMPTY = "";
    public static final String ANNOTATION_UNIQUE_EXPENSE = "Wird zu einzigartiger Ausgabe.";
    public static final String ANNOTATION_ALREADY_IMPORTED = "Ausgabe wurde bereits importiert.";

    private final BooleanProperty selectedForImport = new SimpleBooleanProperty(true);

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();
    private final StringProperty receiver = new SimpleStringProperty();
    private final StringProperty postingText = new SimpleStringProperty();
    private final StringProperty reference = new SimpleStringProperty();
    private final IntegerProperty amount = new SimpleIntegerProperty();
    private final ObjectProperty<Reference<FixedTurnoverDTO>> fixedExpense = new SimpleObjectProperty<>();
    private final StringProperty annotation = new SimpleStringProperty();
    private final BooleanProperty alreadyImported = new SimpleBooleanProperty();

    @Getter
    private final AccountCsvRow accountCsvRow;

    private ImportInformation(AccountCsvRow accountCsvRow) {
        this.accountCsvRow = accountCsvRow;

        date.set(accountCsvRow.getDate());
        receiver.set(accountCsvRow.getReceiver());
        postingText.set(accountCsvRow.getPostingText());
        reference.set(accountCsvRow.getReference());
        amount.set(accountCsvRow.getAmount());

        fixedExpense.addListener((observableValue, oldVal, newVal) -> updateAnnotation());
        alreadyImported.addListener((observableValue, oldVal, newVal) -> updateAnnotation());
        selectedForImport.addListener((observableValue, oldVal, newVal) -> updateAnnotation());
        updateAnnotation();
    }

    private void updateAnnotation() {
        if (alreadyImported.get()) {
            annotation.set(ANNOTATION_ALREADY_IMPORTED);
            return;
        }

        if (!selectedForImport.get()) {
            annotation.set(ANNOTATION_EMPTY);
            return;
        }

        if (fixedExpense.get() == null) {
            annotation.set(ANNOTATION_UNIQUE_EXPENSE);
            return;
        }

        annotation.set(ANNOTATION_EMPTY);
    }

    static ImportInformation from(AccountCsvRow accountCsvRow) {
        return new ImportInformation(accountCsvRow);
    }

    public boolean isSelectedForImport() {
        return selectedForImport.get();
    }

    public boolean hasFixedExpense() {
        return fixedExpenseProperty().get() != null;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public BooleanProperty selectedForImportProperty() {
        return selectedForImport;
    }

    public StringProperty receiverProperty() {
        return receiver;
    }

    public StringProperty postingTextProperty() {
        return postingText;
    }

    public StringProperty referenceProperty() {
        return reference;
    }

    public IntegerProperty amountProperty() {
        return amount;
    }

    public ObjectProperty<Reference<FixedTurnoverDTO>> fixedExpenseProperty() {
        return fixedExpense;
    }

    public StringProperty annotationProperty() {
        return annotation;
    }

    public BooleanProperty alreadyImportedProperty() {
        return alreadyImported;
    }

    @Override
    public RowType getRowType() {
        return RowType.IMPORT;
    }
}
