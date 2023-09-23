package com.solovev.dao.daoImplementations;

import com.solovev.dao.AbstractDAO;
import com.solovev.model.User;

public class UserDao extends AbstractDAO<User> {
    public UserDao() {
        super(User.class);
    }
}
