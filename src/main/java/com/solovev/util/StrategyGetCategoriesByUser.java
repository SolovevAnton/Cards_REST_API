package com.solovev.util;

import com.solovev.dao.daoImplementations.CategoriesDao;
import com.solovev.dto.ResponseResult;
import com.solovev.model.Category;

import java.util.Collection;
import java.util.Map;

public class StrategyGetCategoriesByUser extends StrategyGet<Collection<Category>> {
    public StrategyGetCategoriesByUser(Map<String, String[]> parametersMap) {
        super(parametersMap);
    }

    @Override
    public ResponseResult<Collection<Category>> getResult() {
        try {
            String userIdString = getOneValue("userId");
            long userId = Long.parseLong(userIdString);
            Collection<Category> foundCategories = new CategoriesDao().getByUser(userId);
            getResponseResult().setData(foundCategories);
        } catch (IllegalArgumentException e) {
            getResponseResult().setMessage(e.getMessage());
        }
        return getResponseResult();
    }
}
