package de.timkodiert.mokka.domain.repository;

import java.util.List;
import javax.inject.Inject;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.jspecify.annotations.Nullable;

import de.timkodiert.mokka.domain.model.FixedTurnover_;
import de.timkodiert.mokka.domain.model.MonthYear;
import de.timkodiert.mokka.domain.model.UniqueTurnover;
import de.timkodiert.mokka.domain.model.UniqueTurnover_;
import de.timkodiert.mokka.domain.util.EntityManager;

public class UniqueTurnoverRepository extends Repository<UniqueTurnover> {

    private static final int PAGE_SIZE = 100;

    @Inject
    public UniqueTurnoverRepository(EntityManager entityManager) {
        super(entityManager, UniqueTurnover.class);
    }

    public List<UniqueTurnover> findAllWithoutFixedExpense(MonthYear monthYear) {
        return findAll().stream()
                        .filter(exp -> exp.getFixedTurnover() == null)
                        .filter(exp -> monthYear.containsDate(exp.getDate()))
                        .toList();
    }

    public List<UniqueTurnover> findAllWithoutFixedExpense(int year) {
        return findAll().stream()
                        .filter(exp -> exp.getFixedTurnover() == null)
                        .filter(exp -> exp.getDate().getYear() == year)
                        .toList();
    }

    public List<UniqueTurnover> findByLimitSortedByDateDesc(int limit) {
        var context = getQueryContext();
        var root = context.root();
        var query = context.query();
        query.select(root).orderBy(context.criteriaBuilder().desc(root.get(UniqueTurnover_.date)));
        return executeQuery(query, limit);
    }

    public List<UniqueTurnover> findPageSortedByDateDesc(int page, @Nullable Integer fixedTurnoverId) {
        var context = getQueryContext();
        var root = context.root();
        var query = context.query();

        query.select(root).where(getFixedTurnoverPredicate(fixedTurnoverId, root)).orderBy(context.criteriaBuilder().desc(root.get(UniqueTurnover_.date)));
        return executeQuery(query, page * PAGE_SIZE, PAGE_SIZE);
    }

    public int getNumberOfPages(@Nullable Integer fixedTurnoverId) {
        var context = getQueryContext();
        var root = context.root();
        var query = context.query();
        query.select(root).where(getFixedTurnoverPredicate(fixedTurnoverId, root));
        long count = entityManager.getSession().createQuery(query).getResultCount();
        return (int) Math.ceil((double) count / PAGE_SIZE);
    }

    private Predicate getFixedTurnoverPredicate(Integer fixedTurnoverId, Root<UniqueTurnover> root) {
        var withoutFixedTurnover = root.get(UniqueTurnover_.fixedTurnover).get(FixedTurnover_.id).isNull();
        var withFixedTurnover = entityManager.getSession().getCriteriaBuilder().equal(root.get(UniqueTurnover_.fixedTurnover).get(FixedTurnover_.id), fixedTurnoverId);
        return fixedTurnoverId == null ? withoutFixedTurnover : withFixedTurnover;
    }
}
