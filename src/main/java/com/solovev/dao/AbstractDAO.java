package com.solovev.dao;

import lombok.RequiredArgsConstructor;
import org.hibernate.Session;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Abstract implementation of DAO for concrete DB.
 * DB is chosen based on the SessionFactorySingleton parameters
 */
@RequiredArgsConstructor
public abstract class AbstractDAO<T> implements DAO<T>{

    //creation of factory object
    static {
        SessionFactorySingleton.getInstance();
    }
    private final Class<T> self;

    @Override
    public Optional<T> get(long id) {
        //method itself
        Function<Session,T> get = session -> session.get(self,id);
        Optional<T> result;
        try(AutoCloseableSessionWrapper session = new AutoCloseableSessionWrapper()){
            // manages transactions
            T object = session.beginAndCommitTransaction(get);
            result = Optional.ofNullable(object);
        }
        return result;
    }

    @Override
    public boolean add(T elem) {
        return false;
    }


}
