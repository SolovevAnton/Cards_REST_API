package com.solovev.dao;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class SessionFactorySingleton {
    private static SessionFactory factory;

    synchronized public static SessionFactory getInstance() {
        if (factory == null) {
            factory = new Configuration().configure("hibernatemysql.cfg.xml").buildSessionFactory();
        }
        return factory;
    }
    synchronized public static SessionFactory getInstance(File file) {
        if (factory == null) {
            factory = new Configuration().configure(file).buildSessionFactory();
        }
        return factory;
    }

    synchronized public static void closeAndDeleteInstance() {
        if (factory != null) {
            factory.close();
        }
        factory = null;
    }
}
