package de.timkodiert.mokka.domain.repository;

import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.FixedTurnover;
import de.timkodiert.mokka.domain.util.EntityManager;

public class FixedExpensesRepository extends Repository<FixedTurnover> {

    @Inject
    public FixedExpensesRepository(EntityManager entityManager) {
        super(entityManager, FixedTurnover.class);
    }
}
