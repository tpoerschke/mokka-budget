package de.timkodiert.mokka.importer.account_turnover;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.AccountTurnover;
import de.timkodiert.mokka.domain.model.ImportConfiguration;
import de.timkodiert.mokka.domain.repository.AccountTurnoverRepository;
import de.timkodiert.mokka.domain.repository.FixedExpensesRepository;
import de.timkodiert.mokka.domain.repository.ImportConfigurationsRepository;

public class TurnoverImporterImpl implements TurnoverImporter {

    private final AccountTurnoverRepository accountTurnoverRepository;
    private final FixedExpensesRepository fixedTurnoverRepository;
    private final ImportConfigurationsRepository importConfigurationRepository;
    private final FixedTurnoverLinker fixedTurnoverLinker;
    private final DuplicateFilter duplicateFilter;

    @Inject
    public TurnoverImporterImpl(AccountTurnoverRepository accountTurnoverRepository,
                                FixedExpensesRepository fixedTurnoverRepository,
                                ImportConfigurationsRepository importConfigurationRepository,
                                FixedTurnoverLinker fixedTurnoverLinker,
                                DuplicateFilter duplicateFilter) {
        this.accountTurnoverRepository = accountTurnoverRepository;
        this.fixedTurnoverRepository = fixedTurnoverRepository;
        this.importConfigurationRepository = importConfigurationRepository;
        this.fixedTurnoverLinker = fixedTurnoverLinker;
        this.duplicateFilter = duplicateFilter;
    }

    @Override
    public ImportResult importFromFile(File file) {
        ImportConfiguration importConfig = importConfigurationRepository.findAll().stream().filter(ImportConfiguration::isDefault).findAny().orElseThrow();
        return ImportChain.of(new FileParser(), fixedTurnoverLinker, duplicateFilter).doImport(new ImportContext(file.toPath(), importConfig));
    }

    @Override
    public void doImport(List<ImportInformation> importInformationList) {
        List<AccountTurnover> importsWithFixedExpense = importInformationList.stream()
                                                                             .filter(ImportInformation::isSelectedForImport)
                                                                             .filter(ImportInformation::hasFixedExpense)
                                                                             .map(this::mapImportInfoToFixedTurnover)
                                                                             .toList();

        List<AccountTurnover> importsWithUniqueExpense = importInformationList.stream()
                                                                              .filter(ImportInformation::isSelectedForImport)
                                                                              .filter(Predicate.not(ImportInformation::hasFixedExpense))
                                                                              .map(AccountTurnover::withUniqueTurnover)
                                                                              .toList();

        accountTurnoverRepository.persist(importsWithFixedExpense);
        accountTurnoverRepository.persist(importsWithUniqueExpense);
    }

    private AccountTurnover mapImportInfoToFixedTurnover(ImportInformation info) {
        return AccountTurnover.withFixedTurnover(info, fixedTurnoverRepository.findById(info.fixedExpenseProperty().get().id()));
    }
}
