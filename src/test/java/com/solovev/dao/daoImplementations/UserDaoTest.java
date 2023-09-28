package com.solovev.dao.daoImplementations;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.dao.DAO;
import com.solovev.model.User;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class UserDaoTest {

    @Test
    public void getByIdTest() throws SQLException {
        DAO<User> userDAO = new UserDao();
        long maxUserId = dbSetUpAndTearDown.getMaxIdInDb(USERS_TABLE_NAME);
        long minUserId = dbSetUpAndTearDown.getMinIdInDb(USERS_TABLE_NAME);

        //found
        User firstUser = USERS.get(0);
        User lastUser = USERS.get(USERS.size() - 1);

        assertEquals(firstUser, userDAO.get(minUserId).orElse(null));
        assertEquals(lastUser, userDAO.get(maxUserId).orElse(null));

        //not found
        assertEquals(Optional.empty(), userDAO.get(-1));
        assertEquals(Optional.empty(), userDAO.get(minUserId - 1));
        assertEquals(Optional.empty(), userDAO.get(maxUserId + 1));
    }

    @Test
    public void getAll() throws SQLException {
        DAO<User> userDAO = new UserDao();
        assertEquals(USERS, userDAO.get());

        dbSetUpAndTearDown.clearTable(USERS_TABLE_NAME);

        assertEquals(List.of(), userDAO.get());
    }

    @Nested
    public class getByParamTests {
        @Test
        public void getObjectByParamSuccess() {
            DAO<User> userDAO = new UserDao();

            assertEquals(USERS.get(0), userDAO.getObjectByParam("name", USERS.get(0).getName()).get());
            assertEquals(USERS.get(1), userDAO.getObjectByParam("name", USERS.get(1).getName()).get());
            assertEquals(USERS.get(0), userDAO.getObjectByParam("login", USERS.get(0).getLogin()).get());
        }

        @Test
        public void getObjectByParamNotFound() {
            DAO<User> userDAO = new UserDao();

            assertEquals(Optional.empty(), userDAO.getObjectByParam("name", "non existent"));
        }

        @Test
        public void getObjectsByParamSuccess() {
            DAO<User> userDAO = new UserDao();
            LocalDate today = LocalDate.now();

            assertEquals(USERS, userDAO.getObjectsByParam(Map.of()));
            assertEquals(USERS, userDAO.getObjectsByParam(Map.of("registrationDate",today)));
        }

        @Test
        public void getObjectByParamsSuccess() {
            DAO<User> userDAO = new UserDao();
            User userToFind = USERS.get(0);
            String userLog = userToFind.getLogin();
            String userPass = userToFind.getPassword();

            assertEquals(userToFind, userDAO.getObjectByParam(Map.of("login", userLog, "password", userPass)).get());
            assertEquals(userToFind, userDAO.getObjectByParam(Map.of("login", userLog)).get());
        }

        @Test
        public void getObjectByParamsNotFound() {
            DAO<User> userDAO = new UserDao();

            User userToFind = USERS.get(0);
            String userLog = userToFind.getLogin();
            String userPass = userToFind.getPassword();
            String nonExistentLog = userLog + " corrupted";
            String nonExistentPass = userPass + " corrupted";


            assertEquals(Optional.empty(),
                    userDAO.getObjectByParam(Map.of("login", nonExistentLog)));
            assertEquals(Optional.empty(),
                    userDAO.getObjectByParam(Map.of("login", userLog, "password", nonExistentPass)));

            assertEquals(Optional.empty(),
                    userDAO.getObjectByParam(Map.of("login", nonExistentLog, "password", userPass)));
            assertEquals(Optional.empty(),
                    userDAO.getObjectByParam(Map.of("login", nonExistentLog, "password", nonExistentPass)));

        }
    }

    @Test
    public void delete() throws SQLException {
        DAO<User> userDAO = new UserDao();
        long idToDelete = dbSetUpAndTearDown.getMinIdInDb(USERS_TABLE_NAME);
        User userToDelete = USERS.get(0);

        assumeTrue(userDAO.get().contains(userToDelete));
        assertEquals(userToDelete, userDAO.delete(idToDelete).orElse(null));
        assertFalse(userDAO.get().contains(userToDelete));
        assertEquals(Optional.empty(), userDAO.delete(idToDelete));
    }

    @Test
    public void addSuccessful() throws SQLException {
        DAO<User> userDAO = new UserDao();
        User userToAdd = new User(-1, "addedLog", "addedPass", "addedName");

        assumeFalse(userDAO.get().contains(userToAdd));
        assertTrue(userDAO.add(userToAdd));
        assertEquals(userToAdd, userDAO.get(dbSetUpAndTearDown.getMaxIdInDb(USERS_TABLE_NAME)).get());
    }

    @Test
    public void addUnsuccessful() {
        DAO<User> userDAO = new UserDao();
        User emptyUser = new User();
        User existingUser = USERS.get(0);

        assertThrows(IllegalArgumentException.class, () -> userDAO.add(emptyUser));
        assertThrows(IllegalArgumentException.class, () -> userDAO.add(existingUser));

        //asserts that original table is the same
        assertEquals(USERS, userDAO.get());
    }

    @Test
    public void updateSuccessful() throws SQLException {
        DAO<User> userDAO = new UserDao();
        long idToUpdate = dbSetUpAndTearDown.getMinIdInDb(USERS_TABLE_NAME);
        User originalUser = userDAO.get(idToUpdate).orElse(null);
        User userUpdate = new User(idToUpdate, "updatedLog", "updatedPass", "updatedName");
        assumeTrue(userDAO.get().contains(originalUser));

        assertTrue(userDAO.update(userUpdate));
        assertEquals(userUpdate, userDAO.get(idToUpdate).get());
        assertFalse(userDAO.get().contains(originalUser));
    }

    @Test
    public void updateUnsuccessful() throws SQLException {
        DAO<User> userDAO = new UserDao();
        long idToUpdate = dbSetUpAndTearDown.getMinIdInDb(USERS_TABLE_NAME);
        User originalUser = userDAO.get(idToUpdate).orElse(null);
        User emptyUser = new User();
        //will repeat login from some user
        User corruptedUser = new User(idToUpdate, USERS.get(2).getLogin(), "updatedPass", "updatedName");
        User corruptedIdUser = new User(idToUpdate - 1, USERS.get(2).getLogin(), "updatedPass", "updatedName");
        assumeTrue(userDAO.get().contains(originalUser));

        assertThrows(IllegalArgumentException.class, () -> userDAO.update(corruptedUser));
        assertThrows(IllegalArgumentException.class, () -> userDAO.update(corruptedIdUser));
        assertThrows(IllegalArgumentException.class, () -> userDAO.update(emptyUser));

        //asserts that original table is the same
        assertEquals(USERS, userDAO.get());
    }


    @BeforeEach
    public void setUp() throws SQLException {
        dbSetUpAndTearDown.setUpUsersTableValues(USERS);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dbSetUpAndTearDown.clearTable(USERS_TABLE_NAME);
    }

    private static DBSetUpAndTearDown dbSetUpAndTearDown;
    private final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first"),
            new User(2, "secondLog", "secondPass", "second"),
            new User(3, "thirdLog", "thirdPass", "third")
    );
    private static String USERS_TABLE_NAME;

    @BeforeAll
    public static void tablesAndFactorySetUp() throws SQLException, IOException, ClassNotFoundException {
        dbSetUpAndTearDown = new DBSetUpAndTearDown();
        dbSetUpAndTearDown.dbFactoryAndTablesCreation();

        USERS_TABLE_NAME = dbSetUpAndTearDown.getUSERS_TABLE_NAME();
    }

    @AfterAll
    public static void tablesAndFactoryTearDown() throws SQLException {
        dbSetUpAndTearDown.dbFactoryAndTablesTearDown();
    }

}

