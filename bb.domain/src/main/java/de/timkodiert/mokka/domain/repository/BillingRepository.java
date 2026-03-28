package de.timkodiert.mokka.domain.repository;

import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.Billing;
import de.timkodiert.mokka.domain.util.EntityManager;

public class BillingRepository extends Repository<Billing> {

    @Inject
    public BillingRepository(EntityManager entityManager) {
        super(entityManager, Billing.class);
    }
}
