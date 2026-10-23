package de.timkodiert.mokka.importer.account_turnover;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportResult {

    @Setter
    private ImportStatus status = ImportStatus.SUCCESS;
    private String errorMessageLabel;
    private final List<ImportInformation> importInformationList = new ArrayList<>();

    public static ImportResult create() {
        return new ImportResult();
    }

    public void setErrorStatus(String errorMessageLabel) {
        status = ImportStatus.ERROR;
        this.errorMessageLabel = errorMessageLabel;
    }
}
