package de.timkodiert.mokka.domain.util;

import java.util.Arrays;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import de.timkodiert.mokka.properties.DatabasePropertiesProvider;

@Singleton
public class EntityManager {

    private final DatabasePropertiesProvider dbPropertiesProvider;

    @Getter
    private Session session;

    @Inject
    public EntityManager(DatabasePropertiesProvider dbPropertiesProvider) {
        this.dbPropertiesProvider = dbPropertiesProvider;
    }

    public void openNewSession() {
        if (this.session != null && this.session.isOpen()) {
            this.session.close();
        }
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .applySetting("hibernate.connection.url", dbPropertiesProvider.getFullDbPath())
                .build();
        SessionFactory sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        this.session = sessionFactory.openSession();
    }

    public void closeSession() {
        this.session.close();
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        CriteriaQuery<T> criteriaQuery = this.session.getCriteriaBuilder().createQuery(entityClass);
        criteriaQuery.from(entityClass);
        return this.session.createQuery(criteriaQuery).list();
    }

    public <T> T findById(Class<T> entityClass, int id) {
        return this.session.get(entityClass, id);
    }

    public void persist(Object... objects) {
        this.session.beginTransaction();
        Arrays.stream(objects).forEach(this.session::persist);
        this.session.getTransaction().commit();
    }

    public <T> T merge(T entity) {
        this.session.beginTransaction();
        entity = this.session.merge(entity);
        this.session.getTransaction().commit();
        return entity;
    }

    public void merge(Object... objects) {
        this.session.beginTransaction();
        Arrays.stream(objects).forEach(this.session::merge);
        this.session.getTransaction().commit();
    }

    public void remove(Object... objects) {
        this.session.beginTransaction();
        Arrays.stream(objects).forEach(this.session::remove);
        this.session.getTransaction().commit();
    }

    public void refresh(Object object) {
        this.session.refresh(object);
    }
}
