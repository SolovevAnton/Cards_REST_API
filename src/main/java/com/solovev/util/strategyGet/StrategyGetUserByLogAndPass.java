package com.solovev.util.strategyGet;

import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.dto.ResponseResult;
import com.solovev.model.User;

import java.util.Map;
import java.util.Optional;

public class StrategyGetUserByLogAndPass extends StrategyGet<User> {
    public StrategyGetUserByLogAndPass(Map<String, String[]> parametersMap) {
        super(parametersMap);
    }

    @Override
    public ResponseResult<User> getResult() {
        try{
            String login = getOneValue("login");
            String pass = getOneValue("password");
            Optional<User> foundUser = new UserDao().getUserByLoginAndPass(login,pass);
            configureResponseResult(foundUser, "Cannot find user with this login and password");
        } catch (IllegalArgumentException e){
            getResponseResult().setMessage(e.getMessage());
        }
        return getResponseResult();
    }
}
