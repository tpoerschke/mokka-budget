package de.timkodiert.mokka.importer;

import java.io.File;
import java.io.IOException;

import javafx.collections.ObservableList;

public interface TurnoverImporter {
    TurnoverImporter parse(File file) throws IOException, IllegalStateException;
    TurnoverImporter linkWithExpenses();
    TurnoverImporter filterDuplicates();
    void doImport();
    ObservableList<ImportInformation> getImportInformationList();
}
