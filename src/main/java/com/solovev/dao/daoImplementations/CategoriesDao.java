package com.solovev.dao.daoImplementations;

import com.solovev.dao.AbstractDAO;
import com.solovev.model.Category;

import java.util.Collection;
import java.util.Map;

public class CategoriesDao extends AbstractDAO<Category> {
    public CategoriesDao() {
        super(Category.class);
    }

    /**
     * Gets category by users id
     *
     * @param userId id of the user
     * @return matching categories
     */
    public Collection<Category> getByUser(Long userId) {
        Map<String, Object> params = Map.of("user", userId);
        return getObjectsByParam(params);
    }
}
