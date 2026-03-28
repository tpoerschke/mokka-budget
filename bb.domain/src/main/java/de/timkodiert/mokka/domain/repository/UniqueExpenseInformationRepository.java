package de.timkodiert.mokka.domain.repository;

import java.util.Collection;
import java.util.List;
import javax.inject.Inject;

import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnoverInformation;
import de.timkodiert.mokka.domain.util.EntityManager;

public class UniqueExpenseInformationRepository extends Repository<UniqueTurnoverInformation> {

    @Inject
    public UniqueExpenseInformationRepository(EntityManager entityManager) {
        super(entityManager, UniqueTurnoverInformation.class);
    }

    public List<UniqueTurnoverInformation> findAllWithoutFixedTurnoverAndCategory(MonthYear monthYear) {
        return findAll().stream()
                        .filter(uti -> uti.getExpense().getFixedTurnover() == null)
                        .filter(uti -> monthYear.containsDate(uti.getExpense().getDate()))
                        .filter(uti -> uti.getCategory() == null)
                        .toList();
    }

    @Override
    public void remove(Collection<UniqueTurnoverInformation> entities) {
        // Zunächst die Entity aus ihren Beziehungen lösen
        entities.forEach(entity -> entity.getExpense().getPaymentInformations().remove(entity));
        super.remove(entities);
    }
}
