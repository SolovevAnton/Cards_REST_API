package com.solovev.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaQuery;
import java.util.Collection;
import java.util.Optional;
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
    public boolean add(T elem) {
        return false;
    }


}
