package de.timkodiert.mokka.domain.repository;

import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.ImportRule;
import de.timkodiert.mokka.domain.util.EntityManager;

public class ImportRulesRepository extends Repository<ImportRule> {

    @Inject
    public ImportRulesRepository(EntityManager entityManager) {
        super(entityManager, ImportRule.class);
    }
}
