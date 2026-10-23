package de.timkodiert.mokka.importer.account_turnover;

import java.io.File;
import java.util.List;

public interface TurnoverImporter {
    ImportResult importFromFile(File file);
    void doImport(List<ImportInformation> importInformationList);
}
