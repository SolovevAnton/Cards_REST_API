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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {

    @Test
    public void getByIdTest() throws SQLException {
        DAO<User> userDAO = new UserDao();
        //found
        User firstUser = USERS.get(0);
        User lastUser = USERS.get(USERS.size() - 1);

        assertEquals(firstUser, userDAO.get(getMinIdInDb()).orElse(null));
        assertEquals(lastUser, userDAO.get(getMaxIdInDb()).orElse(null));

        //not found
        assertEquals(Optional.empty(), userDAO.get(-1));
        assertEquals(Optional.empty(), userDAO.get(getMinIdInDb() - 1));
        assertEquals(Optional.empty(), userDAO.get(getMaxIdInDb() + 1));
    }

    @Test
    public void getAll() throws SQLException {
        DAO<User> userDAO = new UserDao();
        assertEquals(USERS, userDAO.get());

        clearTable();

        assertEquals(List.of(), userDAO.get());
    }

    private long getMinIdInDb() throws SQLException {
        String getMinIdSQL = " SELECT MIN(id) FROM " + USERS_TABLE_NAME;
        ResultSet resultSet = connection.createStatement().executeQuery(getMinIdSQL);
        resultSet.next();
        return resultSet.getLong(1);
    }

    private long getMaxIdInDb() throws SQLException {
        String getMaxIdSQL = " SELECT MAX(id) FROM " + USERS_TABLE_NAME;
        ResultSet resultSet = connection.createStatement().executeQuery(getMaxIdSQL);
        resultSet.next();
        return resultSet.getLong(1);
    }

    @BeforeEach
    public void setUp() throws SQLException {
        setUpUsers();
    }

    private void setUpUsers() throws SQLException {
        String SQL = "INSERT INTO " + USERS_TABLE_NAME + "(login,name,password,registration_date) values(?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            for (User user : USERS) {
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
    public void tearDown() throws SQLException {
        clearTable();
    }

    private static void clearTable() throws SQLException {
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
    private static final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first"),
            new User(2, "secondLog", "secondPass", "second"),
            new User(3, "thirdLog", "thirdPass", "third")
    );

    /**
     * Creates factory for DB in hibernate, create all tables if necessary
     * IMPORTANT: in test folder for resources must present hibernatemysql file for tested db, otherwise ioException will be thrown
     */
    @BeforeAll
    public static void dbFactoryAndTablesAndConnectionCreation() throws IOException, ClassNotFoundException, SQLException {
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
    public static void tablesAndConnectionTearDown() throws SQLException {
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