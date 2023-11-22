package com.solovev.servlet;

import com.solovev.dao.daoImplementations.CategoriesDao;
import com.solovev.model.Category;
import com.solovev.util.strategyGet.StrategyGet;
import com.solovev.util.strategyGet.StrategyGetAll;
import com.solovev.util.strategyGet.StrategyGetById;
import com.solovev.util.strategyGet.StrategyGetCategoriesByUser;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/categories")
public class CategoriesServlet extends AbstractServlet<Category> {
    public CategoriesServlet() {
        super(Category.class, new CategoriesDao());
    }

    @Override
    protected Optional<StrategyGet<?>> defineStrategyOfGet(Map<String, String[]> parametersMap) {
        StrategyGet<?> chosenStrategy = null;
        if (parametersMap.isEmpty()) {
            chosenStrategy = new StrategyGetAll<>(parametersMap, getDao());
        } else if (parametersMap.containsKey("id")) {
            chosenStrategy = new StrategyGetById<>(parametersMap, getDao());
        } else if (parametersMap.containsKey("userId")) {
            chosenStrategy = new StrategyGetCategoriesByUser(parametersMap);
        }
        return Optional.ofNullable(chosenStrategy);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = req.getParameter("userId");
        if (userId != null) {

        } else {
            super.doPost(req, resp);
        }
    }
}
