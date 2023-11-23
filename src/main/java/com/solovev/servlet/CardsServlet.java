package com.solovev.servlet;

import com.solovev.dao.daoImplementations.CardsDao;
import com.solovev.dao.daoImplementations.CategoriesDao;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.util.strategyGet.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@WebServlet("/cards")
public class CardsServlet extends AbstractServlet<Card> {
    public CardsServlet() {
        super(Card.class, new CardsDao());
    }

    @Override
    protected Optional<StrategyGet<?>> defineStrategyOfGet(Map<String, String[]> parametersMap) {
        StrategyGet<?> chosenStrategy = null;
        if (parametersMap.isEmpty()) {
            chosenStrategy = new StrategyGetAll<>(parametersMap, getDao());
        } else if (parametersMap.containsKey("id")) {
            chosenStrategy = new StrategyGetById<>(parametersMap, getDao());
        } else if (parametersMap.containsKey("userId")) {
            chosenStrategy = new StrategyGetCardsByUserId(parametersMap);
        } else if (parametersMap.containsKey("categoryId")) {
            chosenStrategy = new StrategyGetCardsByCategoryId(parametersMap);
        }
        return Optional.ofNullable(chosenStrategy);
    }
}
