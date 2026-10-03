package de.timkodiert.mokka.domain.repository;

import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.ImportConfiguration;
import de.timkodiert.mokka.domain.util.EntityManager;

public class ImportConfigurationsRepository extends Repository<ImportConfiguration> {

    @Inject
    public ImportConfigurationsRepository(EntityManager entityManager) {
        super(entityManager, ImportConfiguration.class);
    }
}
