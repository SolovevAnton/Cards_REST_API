package com.solovev.dao.daoImplementations;

import com.solovev.dao.DAO;
import com.solovev.dao.SessionFactorySingleton;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;

import javax.persistence.Table;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    @Test
    public void getByIdTest() {
        DAO<User> userDAO = new UserDao();

        //found
        assertEquals(users.get(0),userDAO.get(1).orElse(null));
        assertEquals(users.get(2),userDAO.get(3).orElse(null));

        //not found
        assertEquals(Optional.empty(),userDAO.get(-1));
        assertEquals(Optional.empty(),userDAO.get(0));
        assertEquals(Optional.empty(),userDAO.get(4));
    }

    @BeforeEach
    private void setUp() throws SQLException {
        setUpUsers(users);
    }

    private void setUpUsers(Collection<User> users) throws SQLException {
        String SQL = "INSERT INTO " + USERS_TABLE_NAME + "(login,name,password,registration_date) values(?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            for (User user : users) {
                statement.setString(1, user.getLogin());
                statement.setString(2, user.getName());
                statement.setString(3, user.getPassword());
                statement.setDate(4, Date.valueOf(user.getRegistrationDate()));

                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    @AfterEach
    private void tearDown() throws SQLException {
        String sqlDelete = "DELETE FROM " + USERS_TABLE_NAME;
        executeStatement(sqlDelete);
    }

    private static void executeStatement(String sqlQuery) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        statement.executeUpdate();
        statement.close();
    }

    private static Connection connection;
    private static final String USERS_TABLE_NAME = User.class.getAnnotation(Table.class).name();
    private static List<User> users = List.of(
            new User(1,"firstLog", "firstPass", "first"),
            new User(2,"secondLog", "secondPass", "second"),
            new User(3,"thirdLog", "thirdPass", "third")
    );

    /**
     * Creates factory for DB in hibernate, create all tables if necessary
     * IMPORTANT: in test folder for resources must present hibernatemysql file for tested db, otherwise ioException will be thrown
     */
    @BeforeAll
    private static void dbFactoryAndTablesAndConnectionCreation() throws IOException, ClassNotFoundException, SQLException {
        //assert the file is presented
        String neededResourceName = "hibernatemysql.cfg.xml";
        if (!Files.exists(Path.of("src", "test", "java", "resources", neededResourceName))) {
            throw new IOException("configuration file: " + neededResourceName + " not found");
        }

        //creates factory and tables
        SessionFactorySingleton.getInstance();

        Class.forName("com.mysql.cj.jdbc.Driver");
        openAndConfigureConnection();
    }

    private static void openAndConfigureConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/test_db";
        String username = "root";
        String password = "root";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
    }

    @AfterAll
    private static void tablesAndConnectionTearDown() throws SQLException {
        dropAllCreatedTables();
        closeConnection();
    }

    private static void dropAllCreatedTables() throws SQLException {
        String cardsTableName = Card.class.getAnnotation(Table.class).name();
        String categoriesTableName = Category.class.getAnnotation(Table.class).name();
        String[] tableNames = {cardsTableName, categoriesTableName, USERS_TABLE_NAME};
        String dropTableSQL = "DROP TABLE IF EXISTS ";

        for (String tableName : tableNames) {
            executeStatement(dropTableSQL + tableName);
        }
    }

    private static void closeConnection() {
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
    }


}