package com.solovev.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.DBSetUpAndTearDown;
import com.solovev.dao.DAO;
import com.solovev.dao.daoImplementations.UserDao;
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UsersServletTest {
    @Nested
    public class doGetTests {
        @Test
        public void doGetByIdSuccess() throws IOException {
            User expectedUser = USERS.get(0);
            Map<String, String[]> parameterMap = Map.of("id", new String[]{String.valueOf(expectedUser.getId())});
            when(request.getParameterMap()).thenReturn(parameterMap);

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>(expectedUser);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 4})
        public void doGetByIdNotFound(int nonExistentId) throws IOException {
            String idToCheck = String.valueOf(nonExistentId);

            Map<String, String[]> parameterMap = Map.of("id", new String[]{idToCheck});
            when(request.getParameterMap()).thenReturn(parameterMap);

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>(usersServlet.getNotFoundIdMsg(nonExistentId));
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetAll() throws IOException {
            when(request.getParameterMap()).thenReturn(Collections.emptyMap());

            usersServlet.doGet(request, response);
            ResponseResult<Collection<User>> expectedResp = new ResponseResult<>(USERS);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetWithNotExistentStrategy() {
            Map<String, String[]> parameterMap = Map.of("login", new String[]{USERS.get(0).getLogin()});
            when(request.getParameterMap()).thenReturn(parameterMap);

            assertAll(() -> usersServlet.doGet(request, response));
        }

        @Test
        public void doGetWithCorruptedId() throws IOException {
            String idToCheck = "NaN";
            Map<String, String[]> parameterMap = Map.of("id", new String[]{idToCheck});
            when(request.getParameterMap()).thenReturn(parameterMap);

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>("java.lang.NumberFormatException: For input string: \"NaN\"");
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetWithNotUniqueId() throws IOException {
            Map<String, String[]> parameterMap = Map.of("id", new String[]{"1", "2"}); // both IDs exist
            when(request.getParameterMap()).thenReturn(parameterMap);

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>("java.lang.IllegalArgumentException: all values must be unique");
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetWithPasswordAndLoginSuccess() throws IOException {
            User expectedUser = USERS.get(0);
            Map<String, String[]> parameterMap = Map.of(
                    "login", new String[]{expectedUser.getLogin()},
                    "password", new String[]{expectedUser.getPassword()});

            when(request.getParameterMap()).thenReturn(parameterMap);

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>(expectedUser);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetWithPasswordAndLoginNotFound() throws IOException {
            User expectedUser = USERS.get(0);
            Map<String, String[]> parameterMap = Map.of(
                    "login", new String[]{expectedUser.getLogin()},
                    "password", new String[]{"non existent pass"});

            when(request.getParameterMap()).thenReturn(parameterMap);

            usersServlet.doGet(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>("Cannot find user with this login and password");
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }
    }

    @Nested
    public class doDeleteTests {
        @Test
        public void deleteSuccess() throws IOException {
            User expectedUser = USERS.get(0);
            int originalSize = userDAO.get().size();
            assumeTrue(userDAO.get().contains(expectedUser));
            when(request.getParameter("id")).thenReturn(String.valueOf(expectedUser.getId()));

            usersServlet.doDelete(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>(expectedUser);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
            assertFalse(userDAO.get().contains(expectedUser));

            //checks deleted only one
            assertEquals(originalSize - 1, userDAO.get().size());
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 4})
        public void deleteNotFound(int nonExistentId) throws IOException {
            String idToCheck = String.valueOf(nonExistentId);
            Collection<User> initialCollection = userDAO.get();

            assumeTrue(userDAO.get(nonExistentId).isEmpty());
            when(request.getParameter("id")).thenReturn(idToCheck);

            usersServlet.doDelete(request, response);

            ResponseResult<User> expectedResp = new ResponseResult<>(usersServlet.getNotFoundIdMsg(nonExistentId));
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());

            //check no changes;
            assertEquals(initialCollection, userDAO.get());
        }

        @Test
        public void deleteNoIdProvided() throws IOException {
            when(request.getParameter("id")).thenReturn(null);

            usersServlet.doDelete(request, response);
            ResponseResult<Collection<User>> expectedResp = new ResponseResult<>(usersServlet.getMessageNoId());
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }
    }

    @Nested
    public class doPutTests {
        private final User userToReplace = USERS.get(0);
        private final long replacementId = userToReplace.getId();
        @Test
        public void doPutJson() throws IOException {
            User userToReplaceWith = new User(replacementId,"replacedLog","replacedPass","replaced");
            requestFactory(userToReplaceWith);

            usersServlet.doPut(request,response);

            ResponseResult<User> expectedUser = new ResponseResult<>(userToReplace);
            assertEquals(expectedUser.jsonToString(),stringWriter.toString());
            assertEquals(userToReplaceWith,userDAO.get(replacementId).orElse(null));
            assertFalse(userDAO.get().contains(userToReplace));
        }

        @Test
        public void noObjectPresent() throws IOException {
            when(request.getHeader("Content-Type")).thenReturn("text/html");
            usersServlet.doPut(request,response);
            assertEquals(USERS,userDAO.get());
        }

        @Test
        public void noIdFoundJsonPut() throws IOException {
            long nonExistentId = -1;
            User userToReplaceWith = new User(nonExistentId,"replacedLog","replacedPass","replaced");
            requestFactory(userToReplaceWith);

            usersServlet.doPut(request,response);

            ResponseResult<User> expectedUser = new ResponseResult<>(usersServlet.getNotFoundIdMsg(nonExistentId));
            assertEquals(expectedUser.jsonToString(),stringWriter.toString());
            assertEquals(USERS,userDAO.get());
        }

        @Test
        public void constrainViolationJson() throws IOException {
            User existentUser = USERS.get(1);
            User toReplace = new User(replacementId, existentUser.getLogin(),existentUser.getPassword(),existentUser.getName());
            requestFactory(toReplace);

            usersServlet.doPut(request,response);

            ResponseResult<User> expectedResult = new ResponseResult<>(usersServlet.getConstrainViolatedMsg());
            assertEquals(expectedResult.jsonToString(),stringWriter.toString());
            assertEquals(USERS,userDAO.get());
        }
    }
    @Nested
    public class doPostTests{
        @Test
        public void doPostJson() throws IOException, ServletException {
            long possibleId = USERS.size() + 1;
            User userToAdd = new User(possibleId,"addedLog", "addedPass", "addedName");
            assumeFalse(userDAO.get().contains(userToAdd));
            requestFactory(userToAdd);

            usersServlet.doPost(request,response);

            ResponseResult<User> expectedResp = new ResponseResult<>(userToAdd);

            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
            assertEquals(userToAdd, userDAO.get(possibleId).get());
            assertEquals(possibleId,userDAO.get().size());
        }
        @Test
        public void noObjectPresent() throws IOException, ServletException {
            when(request.getHeader("Content-Type")).thenReturn("text/html");
            usersServlet.doPost(request,response);
            assertEquals(USERS,userDAO.get());
        }
        @Test
        public void postConstrainViolation() throws IOException {
            User existentUser = USERS.get(1);
            User toReplace = new User(0, existentUser.getLogin(),existentUser.getPassword(),existentUser.getName());
            requestFactory(toReplace);

            usersServlet.doPost(request,response);

            ResponseResult<User> expectedResult = new ResponseResult<>(usersServlet.getConstrainViolatedMsg());
            assertEquals(expectedResult.jsonToString(),stringWriter.toString());
            assertEquals(USERS,userDAO.get());
        }
    }

    private DAO<User> userDAO = new UserDao();
    private UsersServlet usersServlet = new UsersServlet();
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    private StringWriter stringWriter;
    private static final DBSetUpAndTearDown dbSetUpAndTearDown = new DBSetUpAndTearDown();
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

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

    private void requestFactory(User user) throws IOException {
        when(request.getHeader("Content-Type")).thenReturn("application/json");
        String jsonObject = objectMapper.writeValueAsString(user);
        BufferedReader reader = new BufferedReader(new StringReader(jsonObject));
        when(request.getReader()).thenReturn(reader);
    }

    private final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first"),
            new User(2, "secondLog", "secondPass", "second"),
            new User(3, "thirdLog", "thirdPass", "third")
    );
}