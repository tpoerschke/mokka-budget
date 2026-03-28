package de.timkodiert.mokka.domain.repository;

import java.util.Collection;
import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.PaymentInformation;
import de.timkodiert.mokka.domain.util.EntityManager;

public class PaymentInformationsRepository extends Repository<PaymentInformation> {

    @Inject
    public PaymentInformationsRepository(EntityManager entityManager) {
        super(entityManager, PaymentInformation.class);
    }

    @Override
    public void remove(Collection<PaymentInformation> entities) {
        // Zunächst die PaymentInformation aus ihren Beziehungen lösen
        entities.forEach(entity -> entity.getExpense().getPaymentInformations().remove(entity));
        super.remove(entities);
    }
}
