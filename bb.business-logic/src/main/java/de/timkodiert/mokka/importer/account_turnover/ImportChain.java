package de.timkodiert.mokka.importer.account_turnover;

import java.util.stream.IntStream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ImportChain {

    private final ImportStep firstStep;

    public static ImportChain of(ImportStep firstStep, ImportStep... importSteps) {
        if (importSteps.length > 1) {
            IntStream.range(0, importSteps.length - 1).forEach(i -> {
                importSteps[i].setNextStep(importSteps[i + 1]);
            });
        }
        if (importSteps.length > 0) {
            firstStep.setNextStep(importSteps[0]);
        }
        return new ImportChain(firstStep);
    }

    public ImportResult doImport(ImportContext context) {
        ImportResult result = ImportResult.create();
        firstStep.handle(result, context);
        return result;
    }
}
