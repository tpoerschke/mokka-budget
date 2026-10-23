package de.timkodiert.mokka.importer.account_turnover;

import java.util.List;

import jakarta.inject.Inject;

import de.timkodiert.mokka.domain.model.AccountTurnover;
import de.timkodiert.mokka.domain.repository.AccountTurnoverRepository;

public class DuplicateFilter extends ImportStep {

    private final AccountTurnoverRepository accountTurnoverRepository;

    @Inject
    public DuplicateFilter(AccountTurnoverRepository accountTurnoverRepository) {
        this.accountTurnoverRepository = accountTurnoverRepository;
    }

    @Override
    protected void doHandle(ImportResult result, ImportContext context) {
        List<AccountCsvRow> allImports = accountTurnoverRepository.findAll().stream().map(AccountTurnover::asAccountCsvRow).toList();
        result.getImportInformationList().forEach(importInfo -> {
            if (allImports.contains(importInfo.getAccountCsvRow())) {
                importInfo.selectedForImportProperty().set(false);
                importInfo.alreadyImportedProperty().set(true);
            }
        });
    }
}
