package com.solovev.servlet;

import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.model.User;
import com.solovev.util.StrategyGet;
import com.solovev.util.StrategyGetById;
import com.solovev.util.StrategyGetUserByLogAndPass;

import javax.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.Optional;

@WebServlet("/users")
public class UsersServlet extends AbstractServlet<User> {
    public UsersServlet() {
        super(User.class, new UserDao());
    }
    @Override
    protected Optional<StrategyGet<User>> defineGetStrategy(Map<String, String[]> parametersMap) {
        StrategyGet<User> chosenStrategy = null;
        if(parametersMap.containsKey("id")){
            chosenStrategy = new StrategyGetById<>(parametersMap, new UserDao());
        } else if (parametersMap.containsKey("password") && parametersMap.containsKey("login")) {
            chosenStrategy = new StrategyGetUserByLogAndPass(parametersMap);
        }
        return Optional.ofNullable(chosenStrategy);
    }
}
