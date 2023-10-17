package com.solovev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.DBSetUpAndTearDown;
import com.solovev.DataConstants;
import com.solovev.dao.DAO;
import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.model.Category;
import com.solovev.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriesServletTest {

    @Test
    public void doGetByUserIdSuccess(){fail();}
    @Test
    public void doGetByUserIdNotExistent(){fail();}
    @Test
    public void doGetByUserIdNoCategories(){fail();}

    private CategoriesServlet categoriesServlet = new CategoriesServlet();
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private static final DBSetUpAndTearDown dbSetUpAndTearDown = new DBSetUpAndTearDown();

    @BeforeEach
    public void setUp() throws SQLException, IOException, ClassNotFoundException {
        dbSetUpAndTearDown.dbFactoryAndTablesCreation();

        dbSetUpAndTearDown.setUpUsersTableValues(USERS);
        dbSetUpAndTearDown.setUpCategoriesTableValues(CATEGORIES);

        initializeRequest();
    }

    public void initializeRequest() throws IOException {
        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        dbSetUpAndTearDown.dbFactoryAndTablesTearDown();
    }
    private final List<User> USERS = DataConstants.USERS;
    private final List<Category> CATEGORIES = DataConstants.CATEGORIES;

}