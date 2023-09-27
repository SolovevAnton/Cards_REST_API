package com.solovev.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.PropertyValueException;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Abstract implementation of DAO for concrete DB.
 * DB is chosen based on the SessionFactorySingleton parameters
 */
@RequiredArgsConstructor
public abstract class AbstractDAO<T> implements DAO<T> {

    //creation of factory object
    static {
        SessionFactorySingleton.getInstance();
    }

    private final Class<T> self;

    @Override
    public Optional<T> get(long id) {
        //method itself
        Function<Session, T> get = session -> session.get(self, id);
        Optional<T> result;

        try (SessionDecorator sessionDecorator = new SessionDecorator()) {
            // manages transactions
            T object = sessionDecorator.beginAndCommitTransaction(get);
            result = Optional.ofNullable(object);
        }
        return result;
    }

    @Override
    public Collection<T> get() {
            return getObjectsByParam(Map.of());
    }

    @Override
    public <U> Optional<T> getObjectByParam(String paramName, U param) {
        return getObjectByParam(Map.of(paramName,param));
    }
    @Override
    public Optional<T> getObjectByParam(Map<String, Object> paramNamesAndValues) {
        Optional<T> result;
        try (SessionDecorator sessionDecorator = new SessionDecorator()) {
            result = Optional.of(createQuery(sessionDecorator,paramNamesAndValues)
                    .getSingleResult());
        } catch (NoResultException ignored) {
            result = Optional.empty();
        }
        return result;
    }
    @Override
    public <U> Collection<T> getObjectsByParam(String paramName, U param) {
        return getObjectsByParam(Map.of(paramName,param));
    }
    @Override
    public Collection<T> getObjectsByParam(Map<String, Object> paramNamesAndValues) {
        try (SessionDecorator sessionDecorator = new SessionDecorator()) {
            return createQuery(sessionDecorator, paramNamesAndValues)
                    .getResultList();
        }
    }

    private Query<T> createQuery(SessionDecorator sessionDecorator, Map<String, Object> paramNamesAndValues) {
        Session session = sessionDecorator.getSESSION();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(self);
        Root<T> root = criteriaQuery.from(self);
        Predicate[] predicates = new Predicate[paramNamesAndValues.size()];

        int counter = 0;
        for (Map.Entry<String, Object> entry : paramNamesAndValues.entrySet()) {
            predicates[counter++] = criteriaBuilder.equal(root.get(entry.getKey()),entry.getValue());
        }
        criteriaQuery.where(predicates);

        return session.createQuery(criteriaQuery);
    }


    @Override
    public boolean add(T elem) {
        Consumer<Session> add = session -> session.save(elem);

        try (SessionDecorator sessionDecorator = new SessionDecorator()) {
            sessionDecorator.beginAndCommitTransaction(add);
            return true;
        } catch (ConstraintViolationException | PropertyValueException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Optional<T> delete(long id) {
        Optional<T> objectToDelete = get(id);

        if (objectToDelete.isPresent()) {
            try (SessionDecorator sessionDecorator = new SessionDecorator()) {
                Consumer<Session> delete = session -> session.delete(objectToDelete.get());
                sessionDecorator.beginAndCommitTransaction(delete);
            }
        }
        return objectToDelete;
    }

    @Override
    public boolean update(T elem) {
        Consumer<Session> update = session -> session.update(elem);

        try (SessionDecorator sessionDecorator = new SessionDecorator()) {
            sessionDecorator.beginAndCommitTransaction(update);
            return true;
        } catch (PersistenceException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
