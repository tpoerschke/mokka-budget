package de.timkodiert.mokka.importer.account_turnover;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import jakarta.inject.Inject;

import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.model.ImportRule;
import de.timkodiert.mokka.domain.repository.ImportRulesRepository;
import de.timkodiert.mokka.util.ObjectUtils;

import static de.timkodiert.mokka.util.ObjectUtils.ifNull;

public class FixedTurnoverLinker extends ImportStep {

    private final ImportRulesRepository importRulesRepository;

    @Inject
    public FixedTurnoverLinker(ImportRulesRepository importRulesRepository) {
        this.importRulesRepository = importRulesRepository;
    }

    @Override
    protected void doHandle(ImportResult result, ImportContext context) {
        List<ImportRule> rules = importRulesRepository.findAll().stream().filter(ImportRule::isActive).toList();
        result.getImportInformationList().forEach(info -> rules.stream()
                                                               .filter(filterRule(info))
                                                               .findAny()
                                                               .ifPresent(linkWithFixedTurnover(info)));
    }

    private Consumer<ImportRule> linkWithFixedTurnover(ImportInformation importInformation) {
        return rule -> importInformation.fixedExpenseProperty().set(ObjectUtils.ifNull(rule.getLinkedFixedExpense(), FixedTurnover::toReference));
    }

    private Predicate<ImportRule> filterRule(ImportInformation importInformation) {
        return rule -> {
            if (!rule.isActive() || rule.isEmpty()) {
                return false;
            }

            boolean receiverContains = ifNull(rule.getReceiverContains(),
                                              contains -> importInformation.receiverProperty().get().contains(contains),
                                              true);
            boolean referenceContains = ifNull(rule.getReferenceContains(),
                                               contains -> importInformation.referenceProperty().get().contains(contains),
                                               true);
            return receiverContains && referenceContains;
        };
    }
}
