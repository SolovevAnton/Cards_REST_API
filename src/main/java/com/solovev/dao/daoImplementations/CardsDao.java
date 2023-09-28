package com.solovev.dao.daoImplementations;

import com.solovev.dao.AbstractDAO;
import com.solovev.model.Card;
import com.solovev.model.Category;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CardsDao extends AbstractDAO<Card> {
    public CardsDao() {
        super(Card.class);
    }
    /**
     * Gets cards by users id
     *
     * @param userId id of the user
     * @return matching cards
     */
    public Collection<Card> getByUser(Long userId) {
        Collection<Category> categoriesByUser = new CategoriesDao().getByUser(userId);
        Collection<Card> result = new ArrayList<>();

        for(Category category : categoriesByUser){
            long categoryId = category.getId();
            result.addAll(getByCategory(categoryId));
        }

        return result;
    }
    /**
     * Gets cards by category id
     *
     * @param categoryId id of the user
     * @return matching cards
     */
    public Collection<Card> getByCategory(Long categoryId) {
        Map<String, Object> params = Map.of("category", categoryId);
        return getObjectsByParam(params);
    }
}
