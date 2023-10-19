package com.solovev.util.strategyGet;

import com.solovev.dao.daoImplementations.CardsDao;
import com.solovev.dto.ResponseResult;
import com.solovev.model.Card;

import java.util.Collection;
import java.util.Map;

public class StrategyGetCardsByCategoryId extends StrategyGet<Collection<Card>> {
    public StrategyGetCardsByCategoryId(Map<String, String[]> parametersMap) {
        super(parametersMap);
    }

    @Override
    public ResponseResult<Collection<Card>> getResult() {
        try {
            String userIdString = getOneValue("categoryId");
            long cardId = Long.parseLong(userIdString);
            Collection<Card> foundCards = new CardsDao().getByCategory(cardId);
            getResponseResult().setData(foundCards);
        } catch (IllegalArgumentException e) {
            getResponseResult().setMessage(e.getMessage());
        }
        return getResponseResult();
    }
}
