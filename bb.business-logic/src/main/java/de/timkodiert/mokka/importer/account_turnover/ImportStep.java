package de.timkodiert.mokka.importer.account_turnover;

import lombok.Setter;

public abstract class ImportStep {

    @Setter
    private ImportStep nextStep;

    public void handle(ImportResult result, ImportContext context) {
        doHandle(result, context);
        if (result.getStatus() == ImportStatus.ERROR) {
            return;
        }
        if (nextStep != null) {
            nextStep.handle(result, context);
        }
    }

    protected abstract void doHandle(ImportResult result, ImportContext context);
}
