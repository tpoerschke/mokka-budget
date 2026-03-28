package de.timkodiert.mokka.importer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Predicate;
import javax.inject.Inject;

import com.opencsv.bean.CsvToBeanBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import de.timkodiert.mokka.domain.model.AccountTurnover;
import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.ImportRule;
import de.timkodiert.mokka.domain.repository.AccountTurnoverRepository;
import de.timkodiert.mokka.domain.repository.FixedExpensesRepository;
import de.timkodiert.mokka.domain.repository.ImportRulesRepository;

import static de.timkodiert.mokka.importer.AccountCsvRow.SKIP_LINES;
import static de.timkodiert.mokka.util.ObjectUtils.nvl;

public class TurnoverImporterImpl implements TurnoverImporter {

    private final ImportRulesRepository importRulesRepository;
    private final AccountTurnoverRepository accountTurnoverRepository;
    private final FixedExpensesRepository fixedTurnoverRepository;

    @Getter
    private final ObservableList<ImportInformation> importInformationList = FXCollections.observableArrayList();

    @Inject
    public TurnoverImporterImpl(ImportRulesRepository importRulesRepository,
                                AccountTurnoverRepository accountTurnoverRepository,
                                FixedExpensesRepository fixedTurnoverRepository) {
        this.importRulesRepository = importRulesRepository;
        this.accountTurnoverRepository = accountTurnoverRepository;
        this.fixedTurnoverRepository = fixedTurnoverRepository;
    }

    @Override
    public TurnoverImporter parse(File file) throws IOException, IllegalStateException {
        var builder = new CsvToBeanBuilder<AccountCsvRow>(new FileReader(file, StandardCharsets.ISO_8859_1));
        List<AccountCsvRow> imports = builder.withSkipLines(SKIP_LINES).withSeparator(';').withType(AccountCsvRow.class).build().parse();
        importInformationList.setAll(imports.stream().map(ImportInformation::from).toList());
        return this;
    }

    @Override
    public TurnoverImporter linkWithExpenses() {
        List<ImportRule> rules = importRulesRepository.findAll().stream().filter(ImportRule::isActive).toList();
        importInformationList.forEach(info -> rules.stream()
                                                   .filter(filterRule(info))
                                                   .findAny()
                                                   .ifPresent(rule -> info.fixedExpenseProperty().set(nvl(rule.getLinkedFixedExpense(), FixedTurnover::toReference))));
        return this;
    }

    @Override
    public TurnoverImporter filterDuplicates() {
        List<AccountCsvRow> allImports = accountTurnoverRepository.findAll().stream().map(AccountTurnover::asAccountCsvRow).toList();

        importInformationList.forEach(importInfo -> {
            if (allImports.contains(importInfo.getAccountCsvRow())) {
                importInfo.selectedForImportProperty().set(false);
                importInfo.alreadyImportedProperty().set(true);
            }
        });

        return this;
    }

    @Override
    public void doImport() {
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

    private Predicate<ImportRule> filterRule(ImportInformation importInformation) {
        return rule -> {
            if (!rule.isActive() || rule.isEmpty()) {
                return false;
            }

            boolean receiverContains = nvl(rule.getReceiverContains(),
                                           contains -> importInformation.receiverProperty().get().contains(contains),
                                           true);
            boolean referenceContains = nvl(rule.getReferenceContains(),
                                            contains -> importInformation.referenceProperty().get().contains(contains),
                                            true);
            return receiverContains && referenceContains;
        };
    }
}
