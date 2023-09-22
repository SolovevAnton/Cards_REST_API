package com.solovev.dao;


import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Function;

public class AutoCloseableSessionWrapper implements AutoCloseable {
    private final Session session;

    public AutoCloseableSessionWrapper() {
        this.session = SessionFactorySingleton.getInstance().openSession();
    }

    public Session getSession() {
        return session;
    }

    /**
     * starts transaction, executes function and commits transaction;
     * Transaction will be rollback in case of exception
     * @param methodToExecute function to execute
     * @return  result of function execution
     * @param <U> result of function execution
     */
    public <U> U beginAndCommitTransaction(Function<Session,U> methodToExecute){
        Transaction transaction = session.beginTransaction();
        try {
            U result = methodToExecute.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e){
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void close() {
        if(session != null && session.isOpen()){
            session.close();
        }
    }
}
