package com.solovev.servlet;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.dto.ResponseResult;
import com.solovev.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UsersServletTest {
    @Nested
    public class doGetTests {
        @Test
        public void doGetByIdSuccess() throws IOException {
            User expectedUser = USERS.get(0);
            when(request.getParameter("id")).thenReturn(String.valueOf(expectedUser.getId()));

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>(expectedUser);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @ParameterizedTest
        @ValueSource(ints = {0,4})
        public void doGetByIdNotFound(int nonExistentId) throws IOException {
            String idToCheck = String.valueOf(nonExistentId);
            when(request.getParameter("id")).thenReturn(idToCheck);

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>(usersServlet.notFoundIdMessage(idToCheck));
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }
        @Test
        public void doGetAll() throws IOException {
            when(request.getParameter("id")).thenReturn(null);

            usersServlet.doGet(request, response);
            ResponseResult<Collection<User>> expectedResp = new ResponseResult<>(USERS);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }
    }

    private UsersServlet usersServlet = new UsersServlet();
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

    private final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first"),
            new User(2, "secondLog", "secondPass", "second"),
            new User(3, "thirdLog", "thirdPass", "third")
    );
}