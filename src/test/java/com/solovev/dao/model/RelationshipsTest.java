package com.solovev.dao.model;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.dao.daoImplementations.CardsDao;
import com.solovev.dao.daoImplementations.CategoriesDao;
import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;
import com.solovev.DataConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Class to test one to many or many to one etc relationships between models
 */
public class RelationshipsTest {

    @Test
    public void addingTest() throws SQLException {
        User userToAdd = new User(0, "added login", "added pass", "added name");
        Category categoryToAdd = new Category("added category", userToAdd);
        Card cardToAdd = new Card("Q to add", "A to add", categoryToAdd);

        //checks collection behaviour
        assertFalse(userToAdd.getCategories().contains(categoryToAdd));

        //constraint violation scenarios
        assertThrows(IllegalArgumentException.class, () -> cardsDao.add(cardToAdd));
        assertThrows(IllegalArgumentException.class, () -> categoriesDao.add(categoryToAdd));

        //successful scenario
        assertTrue(userDao.add(userToAdd));
        assertTrue(categoriesDao.add(categoryToAdd));
        assertTrue(cardsDao.add(cardToAdd));

        //check addition
        long lastAddedId = USERS.size() + 1;
        User lastUserInDB = userDao.get(lastAddedId).get();

        assertEquals(userToAdd, lastUserInDB);
        assertTrue(categoriesDao.get().contains(categoryToAdd));
        assertTrue(cardsDao.get().contains(cardToAdd));
    }

    @Test
    @Disabled
    public void cascadeDontDeleteTest() {
        Card deletedCard = cardsDao.delete(1).get();

        assertFalse(cardsDao.get().contains(deletedCard));
        assertTrue(isUsersNotChanged());
        assertTrue(isCategoriesNotChanged());

        Category deletedCategory = categoriesDao.delete(1).get();

        assertFalse(categoriesDao.get().contains(deletedCategory));
        assertTrue(isUsersNotChanged());

    }

    private boolean isUsersNotChanged() {
        return userDao.get().equals(USERS);
    }

    private boolean isCategoriesNotChanged() {
        return categoriesDao.get().equals(CATEGORIES);
    }

    @Test
    public void cascadeDeleteTest() {
        long categoryIdToDelete = 2L;
        long userIdToDelete = 2L;
        Collection<Card> cardsForCategory = cardsDao.getByCategory(categoryIdToDelete);
        Collection<Category> categoriesForUser = categoriesDao.getByUser(userIdToDelete);

        assumeTrue(cardsDao.get().containsAll(cardsForCategory));
        assumeTrue(categoriesDao.get().containsAll(categoriesForUser));

        categoriesDao.delete(categoryIdToDelete);
        assertFalse(cardsDao.get().removeAll(cardsForCategory));

        userDao.delete(userIdToDelete);
        assertFalse(categoriesDao.get().removeAll(categoriesForUser));

        //check if all deleted when user is deleted
        userDao.delete(1);

        assertTrue(categoriesDao.get().isEmpty());
        assertTrue(cardsDao.get().isEmpty());
    }

    @Test
    public void collectionInitiationInUser() {
        //working ONLY with Eager fetch
        Collection<Category> categoriesForFirstUser = List.of(CATEGORIES.get(0), CATEGORIES.get(1));
        Collection<Category> categoriesForSecondUser = List.of(CATEGORIES.get(2));
        User firstUser = userDao.get(1).get();
        User secondUser = userDao.get(2).get();
        User thirdUser = userDao.get(3).get();

        assertEquals(categoriesForFirstUser, firstUser.getCategories());
        assertEquals(categoriesForSecondUser, secondUser.getCategories());
        assertEquals(List.of(), thirdUser.getCategories());
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

    private final UserDao userDao = new UserDao();
    private final CategoriesDao categoriesDao = new CategoriesDao();
    private final CardsDao cardsDao = new CardsDao();
    private final DBSetUpAndTearDown dbSetUpAndTearDown = new DBSetUpAndTearDown();
    private final List<User> USERS = DataConstants.USERS;
    private final List<Category> CATEGORIES = DataConstants.CATEGORIES;
    private final List<Card> CARDS = DataConstants.CARDS;
}
