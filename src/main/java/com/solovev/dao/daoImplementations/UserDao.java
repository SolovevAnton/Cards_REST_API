package com.solovev.dao.daoImplementations;

import com.solovev.dao.AbstractDAO;
import com.solovev.model.User;

import java.util.Map;
import java.util.Optional;

public class UserDao extends AbstractDAO<User> {
    public UserDao() {
        super(User.class);
    }
    public Optional<User> getUserByLoginAndPass(String login, String password){
        Map<String, Object> parametersAndValues = Map.of("login",login,"password",password);
        return getObjectByParam(parametersAndValues);
    }
    public Optional<User> getUserByHashAndId(String id, String hash){
        Map<String, Object> parametersAndValues = Map.of("id",id,"cookieHash",hash);
        return getObjectByParam(parametersAndValues);
    }
}
