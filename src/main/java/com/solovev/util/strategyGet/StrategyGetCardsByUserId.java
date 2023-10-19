package com.solovev.util.strategyGet;

import com.solovev.dao.daoImplementations.CardsDao;
import com.solovev.dto.ResponseResult;
import com.solovev.model.Card;

import java.util.Collection;
import java.util.Map;

public class StrategyGetCardsByUserId extends StrategyGet<Collection<Card>> {
    public StrategyGetCardsByUserId(Map<String, String[]> parametersMap) {
        super(parametersMap);
    }

    @Override
    public ResponseResult<Collection<Card>> getResult() {
        try {
            String userIdString = getOneValue("userId");
            long userId = Long.parseLong(userIdString);
            Collection<Card> foundCards = new CardsDao().getByUser(userId);
            getResponseResult().setData(foundCards);
        } catch (IllegalArgumentException e) {
            getResponseResult().setMessage(e.getMessage());
        }
        return getResponseResult();
    }
}
