package com.solovev.servlet;

import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.model.User;

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
            chosenStrategy = getById(parametersMap);
        } else if (parametersMap.containsKey("password") && parametersMap.containsKey("login")) {
            String login = getOneValue(parametersMap,"login");
            String pass = getOneValue(parametersMap,"password");
            UserDao userDao = new UserDao();
            chosenStrategy = () -> userDao.getUserByLoginAndPass(login,pass);
        }
        return Optional.ofNullable(chosenStrategy);
    }
}
