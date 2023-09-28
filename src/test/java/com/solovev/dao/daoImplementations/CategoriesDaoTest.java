package com.solovev.dao.daoImplementations;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.model.Category;
import com.solovev.model.User;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoriesDaoTest {

    @Test
    public void getByUserId() throws SQLException {
        CategoriesDao categoriesDao = new CategoriesDao();
        Collection<Category> firstUserCategories = CATEGORIES.stream().filter(category -> category.getUser().getId() == USERS.get(0).getId()).toList();
        Collection<Category> lastUserCategories = CATEGORIES.stream().filter(category -> category.getUser().getId() == USERS.get(USERS.size()-1).getId()).toList();
        long firstUserId = dbSetUpAndTearDown.getMinIdInDb(USERS_TABLE_NAME);
        long lastUserId = dbSetUpAndTearDown.getMaxIdInDb(USERS_TABLE_NAME);
        long nonExistentUserId = firstUserId - 1;

        assertEquals(firstUserCategories,categoriesDao.getByUser(firstUserId));
        assertEquals(lastUserCategories,categoriesDao.getByUser(lastUserId));
        assertEquals(List.of(),categoriesDao.getByUser(nonExistentUserId));

    }
    @BeforeEach
    public void setUp() throws SQLException {
        dbSetUpAndTearDown.setUpUsersTableValues(USERS);
        dbSetUpAndTearDown.setUpCategoriesTableValues(CATEGORIES);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dbSetUpAndTearDown.clearTable(CATEGORIES_TABLE_NAME);
        dbSetUpAndTearDown.clearTable(USERS_TABLE_NAME);
    }

    private static DBSetUpAndTearDown dbSetUpAndTearDown;
    private final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first"),
            new User(2, "secondLog", "secondPass", "second"),
            new User(3, "thirdLog", "thirdPass", "third")
    );
    private final List<Category> CATEGORIES = List.of(
            new Category("firstCat",USERS.get(0)),
            new Category("secondCat",USERS.get(0)),
            new Category("thirdCat",USERS.get(1))
    );
    private static String USERS_TABLE_NAME;
    private static String CATEGORIES_TABLE_NAME;

    @BeforeAll
    public static void tablesAndFactorySetUp() throws SQLException, IOException, ClassNotFoundException {
        dbSetUpAndTearDown = new DBSetUpAndTearDown();
        dbSetUpAndTearDown.dbFactoryAndTablesCreation();

        USERS_TABLE_NAME = dbSetUpAndTearDown.getUSERS_TABLE_NAME();
        CATEGORIES_TABLE_NAME = dbSetUpAndTearDown.getCATEGORIES_TABLE_NAME();
    }

    @AfterAll
    public static void tablesAndFactoryTearDown() throws SQLException {
        dbSetUpAndTearDown.dbFactoryAndTablesTearDown();
    }
}