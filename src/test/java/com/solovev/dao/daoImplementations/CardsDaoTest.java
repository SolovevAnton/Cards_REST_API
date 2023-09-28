package com.solovev.dao.daoImplementations;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardsDaoTest {

    @Test
    public void getByCategory() {
        long firstCategoryId = 1;
        long lastCategoryId = CATEGORIES.size();
        long notExistentCategoryId = lastCategoryId + 1;
        Collection<Card> firstCategoryCards = CARDS.stream().filter(card -> card.getCategory().getId() == firstCategoryId).toList();
        Collection<Card> lastCategoryCards = CARDS.stream().filter(card -> card.getCategory().getId() == lastCategoryId).toList();

        assertEquals(firstCategoryCards, cardsDao.getByCategory(firstCategoryId));
        assertEquals(lastCategoryCards, cardsDao.getByCategory(lastCategoryId));
        assertEquals(List.of(), cardsDao.getByCategory(notExistentCategoryId));
    }

    @Test
    public void getByUser() {
        long firstUserId = 1;
        long lastUserId = USERS.size();
        long nonExistentUser = firstUserId - 1;
        Collection<Card> firstUserCards = CARDS.stream().filter(card -> card.getCategory().getUser().getId() == firstUserId).toList();
        Collection<Card> lastUserCards = CARDS.stream().filter(card -> card.getCategory().getUser().getId() == lastUserId).toList();

        assertEquals(firstUserCards, cardsDao.getByUser(firstUserId));
        assertEquals(lastUserCards, cardsDao.getByUser(lastUserId));
        assertEquals(List.of(), cardsDao.getByUser(nonExistentUser));
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException, ClassNotFoundException {
        dbSetUpAndTearDown.dbFactoryAndTablesCreation();

        dbSetUpAndTearDown.setUpUsersTableValues(USERS);
        dbSetUpAndTearDown.setUpCategoriesTableValues(CATEGORIES);
        dbSetUpAndTearDown.setUpCardsTableValues(CARDS);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dbSetUpAndTearDown.dbFactoryAndTablesTearDown();
    }

    private final CardsDao cardsDao = new CardsDao();
    private final DBSetUpAndTearDown dbSetUpAndTearDown = new DBSetUpAndTearDown();
    private final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first"),
            new User(2, "secondLog", "secondPass", "second"),
            new User(3, "thirdLog", "thirdPass", "third")
    );
    private final List<Category> CATEGORIES = List.of(
            new Category(1, "firstCat", USERS.get(0)),
            new Category(2, "secondCat", USERS.get(0)),
            new Category(3, "thirdCat", USERS.get(1))
    );
    private final List<Card> CARDS = List.of(
            new Card("Q1", "A1", CATEGORIES.get(0)),
            new Card("Q2", "A2", CATEGORIES.get(0)),
            new Card("Q3", "A3", CATEGORIES.get(1))
    );
}