package com.solovev.dao;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactorySingleton {
    private static SessionFactory instance;

    synchronized public static SessionFactory getInstance(){
        if (instance == null)     {
            instance = new Configuration().configure("hibernatemysql.cfg.xml").buildSessionFactory();
        }
        return instance;
    }
}
