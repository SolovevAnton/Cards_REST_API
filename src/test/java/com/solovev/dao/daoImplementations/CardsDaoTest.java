package com.solovev.dao.daoImplementations;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;
import com.solovev.DataConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

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

    @Nested
    public class ConstrainTests {
        @Test
        public void successfullyAdded() {
            Card cardToAdd = new Card("QAdd", CARDS.get(0).getAnswer(), CATEGORIES.get(0));
            Card otherToAdd = new Card(CARDS.get(0).getQuestion(), CARDS.get(0).getAnswer(), CATEGORIES.get(CATEGORIES.size() - 1));

            assumeFalse(cardsDao.get().contains(cardToAdd));
            assertTrue(cardsDao.add(cardToAdd));
            assertTrue(cardsDao.get().contains(cardToAdd));

            assumeFalse(cardsDao.get().contains(otherToAdd));
            assertTrue(cardsDao.add(otherToAdd));
            assertTrue(cardsDao.get().contains(otherToAdd));
        }

        @Test
        public void nonNullViolated() {
            Card emptyCard = new Card();
            assertThrows(IllegalArgumentException.class, () -> cardsDao.add(emptyCard));
            assertTrue(checkTableDidntChange());
        }

        @Test
        public void constantViolations() {
            Card existedCard = CARDS.get(0);
            Card notUniqueFields = new Card(existedCard.getId(), existedCard.getQuestion(), existedCard.getAnswer(), existedCard.getCategory());

            assertTrue(cardsDao.get().contains(existedCard));
            assertThrows(IllegalArgumentException.class, () -> cardsDao.add(existedCard));
            assertTrue(checkTableDidntChange());

            assertTrue(cardsDao.get().contains(notUniqueFields));
            assertThrows(IllegalArgumentException.class, () -> cardsDao.add(notUniqueFields));
            assertTrue(checkTableDidntChange());
        }
    }

    private boolean checkTableDidntChange() {
        return cardsDao.get().equals(CARDS);
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
    private final List<User> USERS = DataConstants.USERS;
    private final List<Category> CATEGORIES = DataConstants.CATEGORIES;
    private final List<Card> CARDS = DataConstants.CARDS;
}