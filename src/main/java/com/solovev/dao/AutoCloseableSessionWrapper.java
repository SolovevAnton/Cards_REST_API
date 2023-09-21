package com.solovev.dao;


import org.hibernate.Session;

public class AutoCloseableSessionWrapper implements AutoCloseable {
    private final Session session;

    public AutoCloseableSessionWrapper() {
        this.session = SessionFactorySingleton.getInstance().openSession();
    }

    public Session getSession() {
        return session;
    }

    @Override
    public void close() {
        if(session != null && session.isOpen()){
            session.close();
        }
    }
}
