package de.timkodiert.mokka.domain.repository;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;

import de.timkodiert.mokka.domain.util.CriteriaContext;
import de.timkodiert.mokka.domain.util.EntityManager;

public abstract class Repository<T> {

    protected final EntityManager entityManager;
    protected final Class<T> entityType;

    protected Repository(EntityManager entityManager, Class<T> entityType) {
        this.entityManager = entityManager;
        this.entityType = entityType;
    }

    public List<T> findAll() {
        return this.entityManager.findAll(entityType);
    }

    public T findById(int id) {
        return this.entityManager.findById(entityType, id);
    }

    public void persist(T entity) {
        this.persist(List.of(entity));
    }

    public void persist(Collection<T> entities) {
        entities.forEach(this.entityManager::persist);
    }

    public T merge(T entity) {
        return this.entityManager.merge(entity);
    }

    public void merge(Collection<T> entities) {
        entities.forEach(this.entityManager::merge);
    }

    public void remove(T entity) {
        this.remove(List.of(entity));
    }

    public void remove(Collection<T> entities) {
        entities.forEach(this.entityManager::remove);
    }

    protected CriteriaContext<T> getQueryContext() {
        Session session = entityManager.getSession();
        CriteriaQuery<T> criteriaQuery = session.getCriteriaBuilder().createQuery(entityType);
        Root<T> root = criteriaQuery.from(entityType);
        return new CriteriaContext<>(criteriaQuery, session.getCriteriaBuilder(), root);
    }

    protected List<T> executeQuery(CriteriaQuery<T> criteriaQuery, int limit) {
        return entityManager.getSession().createQuery(criteriaQuery).setMaxResults(limit).getResultList();
    }

    protected List<T> executeQuery(CriteriaQuery<T> criteriaQuery, int firstResult, int limit) {
        return entityManager.getSession().createQuery(criteriaQuery).setFirstResult(firstResult).setMaxResults(limit).getResultList();
    }
}
