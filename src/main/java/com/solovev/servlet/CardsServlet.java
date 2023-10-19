package com.solovev.servlet;

import com.solovev.dao.DAO;
import com.solovev.dao.daoImplementations.CardsDao;
import com.solovev.dto.DTO;
import com.solovev.model.Card;
import com.solovev.util.StrategyGet;

import javax.servlet.annotation.WebServlet;
import java.util.Map;
import java.util.Optional;
@WebServlet("/cards")
public class CardsServlet extends AbstractServlet<Card> {
    public CardsServlet() {
        super(Card.class, new CardsDao());
    }

    @Override
    protected Optional<StrategyGet<?>> defineStrategyOfGet(Map<String, String[]> parametersMap) {
        return Optional.empty();
    }
}
