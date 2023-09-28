package com.solovev;

import com.solovev.dao.SessionFactorySingleton;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;
import lombok.AccessLevel;
import lombok.Getter;

import javax.persistence.Table;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Collection;

/**
 * Class to be used by all test methods to set up and tear down DB
 */
@Getter
public class DBSetUpAndTearDown {
    @Getter(AccessLevel.NONE)
    private Connection connection;
    private final String CARDS_TABLE_NAME = Card.class.getAnnotation(Table.class).name();
    private final String CATEGORIES_TABLE_NAME = Category.class.getAnnotation(Table.class).name();
    private final String USERS_TABLE_NAME = User.class.getAnnotation(Table.class).name();

    /**
     * Creates factory for DB in hibernate, create all tables if necessary
     * IMPORTANT: in test folder for resources must present hibernatemysql file for tested db, otherwise ioException will be thrown
     */
    public void dbFactoryAndTablesCreation() throws IOException, ClassNotFoundException, SQLException {
        //assert the file is presented
        String neededResourceName = "hibernatemysql.cfg.xml";
        if (!Files.exists(Path.of("src", "test", "java", "resources", neededResourceName))) {
            throw new IOException("configuration file: " + neededResourceName + " not found");
        }

        //creates factory and tables
        SessionFactorySingleton.getInstance();

        Class.forName("com.mysql.cj.jdbc.Driver");
        openConnection();
    }

    private void openConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/test_db";
        String username = "root";
        String password = "root";

        connection = DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * Add all users from collection to table
     */
    public void setUpUsersTableValues(Collection<User> users) throws SQLException {
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
    public void setUpCategoriesTableValues(Collection<Category> categories) throws SQLException {
        String SQL = "INSERT INTO " + CATEGORIES_TABLE_NAME + "(name,user_id) values(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            for (Category category : categories) {
                statement.setString(1,category.getName());
                statement.setLong(2, category.getUser().getId());

                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
    public void setUpCardsTableValues(Collection<Card> cards) throws SQLException {
        String SQL = "INSERT INTO " + CARDS_TABLE_NAME + "(question,answer,category_id,creation_date) values(?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(SQL)) {
            for (Card card : cards) {
                statement.setString(1,card.getQuestion());
                statement.setString(2, card.getAnswer());
                statement.setLong(3,card.getCategory().getId());
                statement.setDate(4, Date.valueOf(card.getCreationDate()));

                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public long getMinIdInDb(String tableName) throws SQLException {
        String getMinIdSQL = " SELECT MIN(id) FROM " + tableName;
        ResultSet resultSet = connection.createStatement().executeQuery(getMinIdSQL);
        resultSet.next();
        return resultSet.getLong(1);
    }

    public long getMaxIdInDb(String tableName) throws SQLException {
        String getMaxIdSQL = " SELECT MAX(id) FROM " + tableName;
        ResultSet resultSet = connection.createStatement().executeQuery(getMaxIdSQL);
        resultSet.next();
        return resultSet.getLong(1);
    }

    public void clearTable(String tableName) throws SQLException {
        String sqlDelete = "DELETE FROM " + tableName;
        executeStatement(sqlDelete);
    }

    public void dbFactoryAndTablesTearDown() throws SQLException {
        dropAllTables();
        closeConnection();
        SessionFactorySingleton.closeAndDeleteInstance();
    }

    private void dropAllTables() throws SQLException {
        String[] tableNames = {CARDS_TABLE_NAME, CATEGORIES_TABLE_NAME, USERS_TABLE_NAME};
        String dropTableSQL = "DROP TABLE IF EXISTS ";

        for (String tableName : tableNames) {
            executeStatement(dropTableSQL + tableName);
        }
    }

    private void executeStatement(String sqlQuery) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(sqlQuery);
        statement.executeUpdate();
        statement.close();
    }

    public void closeConnection() {
        if (connection != null)
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
    }
}
