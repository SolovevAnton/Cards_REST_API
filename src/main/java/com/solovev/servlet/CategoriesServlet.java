package com.solovev.servlet;

import com.solovev.dao.DAO;
import com.solovev.dao.daoImplementations.CategoriesDao;
import com.solovev.dto.DTO;
import com.solovev.model.Category;
import com.solovev.util.StrategyGet;

import java.util.Map;
import java.util.Optional;

public class CategoriesServlet extends AbstractServlet<Category> {
    public CategoriesServlet() {
        super(Category.class, new CategoriesDao());
    }

    @Override
    protected Optional<StrategyGet<?>> defineStrategyOfGet(Map<String, String[]> parametersMap) {
        return Optional.empty();
    }
}
