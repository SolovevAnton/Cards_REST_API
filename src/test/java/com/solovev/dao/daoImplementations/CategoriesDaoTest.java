package com.solovev.dao.daoImplementations;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.model.Category;
import com.solovev.model.User;
import com.solovev.servlet.DataConstants;
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

public class CategoriesDaoTest {

    @Test
    public void getByUserId() throws SQLException {
        Collection<Category> firstUserCategories = CATEGORIES.stream().filter(category -> category.getUser().getId() == USERS.get(0).getId()).toList();
        Collection<Category> lastUserCategories = CATEGORIES.stream().filter(category -> category.getUser().getId() == USERS.get(USERS.size() - 1).getId()).toList();
        long firstUserId = USERS.get(0).getId();
        long lastUserId = USERS.size();
        long nonExistentUserId = firstUserId - 1;

        assertEquals(firstUserCategories, categoriesDao.getByUser(firstUserId));
        assertEquals(lastUserCategories, categoriesDao.getByUser(lastUserId));
        assertEquals(List.of(), categoriesDao.getByUser(nonExistentUserId));

    }

    @Nested
    public class ConstraintTests {
        @Test
        public void successfulAdd(){
            Category categoryToAdd = new Category("addedCat", USERS.get(0));

            assumeFalse(categoriesDao.get().contains(categoryToAdd));
            assertTrue(categoriesDao.add(categoryToAdd));
            assertTrue(categoriesDao.get().contains(categoryToAdd));
        }
        @Test
        public void constrainViolated(){
            Category existedCategory = CATEGORIES.get(0);

            assertTrue(categoriesDao.get().contains(existedCategory));
            assertThrows(IllegalArgumentException.class, () -> categoriesDao.add(existedCategory));
            assertTrue(checkTableDidntChange());
        }
        @Test
        public void nonNullViolated(){
            Category nullFieldsCategory = new Category();

            assertThrows(IllegalArgumentException.class, () -> categoriesDao.add(nullFieldsCategory));
            assertTrue(checkTableDidntChange());
        }
    }
    private boolean checkTableDidntChange(){
        return categoriesDao.get().equals(CATEGORIES);
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException, ClassNotFoundException {
        dbSetUpAndTearDown.dbFactoryAndTablesCreation();

        dbSetUpAndTearDown.setUpUsersTableValues(USERS);
        dbSetUpAndTearDown.setUpCategoriesTableValues(CATEGORIES);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dbSetUpAndTearDown.dbFactoryAndTablesTearDown();
    }
    private final CategoriesDao categoriesDao = new CategoriesDao();
    private final DBSetUpAndTearDown dbSetUpAndTearDown = new DBSetUpAndTearDown();
    private final List<User> USERS = DataConstants.USERS;
    private final List<Category> CATEGORIES = DataConstants.CATEGORIES;

}