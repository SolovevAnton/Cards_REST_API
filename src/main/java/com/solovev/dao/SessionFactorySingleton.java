package com.solovev.dao;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.File;

public class SessionFactorySingleton {
    private static SessionFactory factory;
    private static File configurationFile;

    synchronized public static SessionFactory getInstance() {
        if (factory == null) {
            factory = new Configuration().configure("hibernatemysql.cfg.xml").buildSessionFactory();
        }
        return factory;
    }

    synchronized public static SessionFactory getInstance(File file) {
        if(!file.equals(configurationFile)){
            closeAndDeleteInstance();
        }
        if (factory == null) {
            factory = new Configuration().configure(file).buildSessionFactory();
            configurationFile = file;
        }
        return factory;
    }

    synchronized public static void closeAndDeleteInstance() {
        if (factory != null) {
            factory.close();
        }
        configurationFile = null;
        factory = null;
    }
}
