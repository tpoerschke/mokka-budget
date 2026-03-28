package de.timkodiert.mokka.domain.repository;

import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.AccountTurnover;
import de.timkodiert.mokka.domain.util.EntityManager;

public class AccountTurnoverRepository extends Repository<AccountTurnover> {

    @Inject
    public AccountTurnoverRepository(EntityManager entityManager) {
        super(entityManager, AccountTurnover.class);
    }
}
