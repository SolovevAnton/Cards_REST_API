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
import javax.persistence.criteria.Root;
import java.util.Collection;
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

        try (AutoCloseableSessionWrapper autoCloseableSessionWrapper = new AutoCloseableSessionWrapper()) {
            // manages transactions
            T object = autoCloseableSessionWrapper.beginAndCommitTransaction(get);
            result = Optional.ofNullable(object);
        }
        return result;
    }

    @Override
    public Collection<T> get() {
        try (AutoCloseableSessionWrapper autoCloseableSessionWrapper = new AutoCloseableSessionWrapper()) {
            Session session = autoCloseableSessionWrapper.getSESSION();

            CriteriaQuery<T> query = session.getCriteriaBuilder().createQuery(self);
            query.from(self);
            return session.createQuery(query).getResultList();
        }
    }

    @Override
    public <U> Optional<T> getObjectByParam(String paramName, U param) {
        Optional<T> result;
        try (AutoCloseableSessionWrapper autoCloseableSessionWrapper = new AutoCloseableSessionWrapper()) {
            result = Optional.of(createQuery(autoCloseableSessionWrapper, paramName, param)
                    .getSingleResult());
        } catch (NoResultException ignored) {
            result = Optional.empty();
        }
        return result;
    }

    @Override
    public <U> Collection<T> getObjectsByParam(String paramName, U param) {
        try (AutoCloseableSessionWrapper autoCloseableSessionWrapper = new AutoCloseableSessionWrapper()) {
            return createQuery(autoCloseableSessionWrapper, paramName, param)
                    .getResultList();
        }
    }

    @Override
    public Optional<T> getObjectByParams(String[] paramNames, Object[] params) {
        return Optional.empty();
    }

    private <U> Query<T> createQuery(AutoCloseableSessionWrapper autoCloseableSessionWrapper, String paramName, U param) {
        Session session = autoCloseableSessionWrapper.getSESSION();
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(self);
        Root<T> root = criteriaQuery.from(self);

        criteriaQuery.where(
                criteriaBuilder.equal(root.get(paramName), param));
        return session.createQuery(criteriaQuery);
    }


    @Override
    public boolean add(T elem) {
        Consumer<Session> add = session -> session.save(elem);

        try (AutoCloseableSessionWrapper autoCloseableSessionWrapper = new AutoCloseableSessionWrapper()) {
            autoCloseableSessionWrapper.beginAndCommitTransaction(add);
            return true;
        } catch (ConstraintViolationException | PropertyValueException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public Optional<T> delete(long id) {
        Optional<T> objectToDelete = get(id);

        if (objectToDelete.isPresent()) {
            try (AutoCloseableSessionWrapper autoCloseableSessionWrapper = new AutoCloseableSessionWrapper()) {
                Consumer<Session> delete = session -> session.delete(objectToDelete.get());
                autoCloseableSessionWrapper.beginAndCommitTransaction(delete);
            }
        }
        return objectToDelete;
    }

    @Override
    public boolean update(T elem) {
        Consumer<Session> update = session -> session.update(elem);

        try (AutoCloseableSessionWrapper autoCloseableSessionWrapper = new AutoCloseableSessionWrapper()) {
            autoCloseableSessionWrapper.beginAndCommitTransaction(update);
            return true;
        } catch (PersistenceException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
