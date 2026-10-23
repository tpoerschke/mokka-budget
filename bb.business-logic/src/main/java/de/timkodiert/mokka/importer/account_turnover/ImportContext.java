package de.timkodiert.mokka.importer.account_turnover;

import java.nio.file.Path;

import de.timkodiert.mokka.domain.model.ImportConfiguration;

public record ImportContext(Path filePath, ImportConfiguration configuration) {
}
