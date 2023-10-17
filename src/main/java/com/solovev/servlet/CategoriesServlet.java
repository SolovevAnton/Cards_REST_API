package com.solovev.servlet;

import com.solovev.dao.daoImplementations.CategoriesDao;
import com.solovev.model.Category;
import com.solovev.util.*;

import java.util.Map;
import java.util.Optional;

public class CategoriesServlet extends AbstractServlet<Category> {
    public CategoriesServlet() {
        super(Category.class, new CategoriesDao());
    }

    @Override
    protected Optional<StrategyGet<?>> defineStrategyOfGet(Map<String, String[]> parametersMap) {
        StrategyGet<?> chosenStrategy = null;
        if (parametersMap.isEmpty()) {
            chosenStrategy = new StrategyGetAll<>(parametersMap,getDao());
        } else if (parametersMap.containsKey("id")) {
            chosenStrategy = new StrategyGetById<>(parametersMap, getDao());
        } else if (parametersMap.containsKey("userId")) {
            chosenStrategy = new StrategyGetCategoriesByUser(parametersMap);
        }
        return Optional.ofNullable(chosenStrategy);
    }
}
