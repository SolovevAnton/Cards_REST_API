package com.solovev.servlet;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.DataConstants;
import com.solovev.dto.ResponseResult;
import com.solovev.model.Category;
import com.solovev.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriesServletTest {

    @Test
    public void doGetByUserIdSuccess() throws IOException {
        User toFindCategoriesFor = USERS.get(0);
        long userIdToLook = toFindCategoriesFor.getId();
        Collection<Category> expectedCategories = CATEGORIES.stream().filter(c -> c.getUser().equals(toFindCategoriesFor)).toList();
        Map<String, String[]> parameterMap = Map.of("userId", new String[]{String.valueOf(userIdToLook)});
        when(request.getParameterMap()).thenReturn(parameterMap);

        categoriesServlet.doGet(request, response);

        ResponseResult<Collection<Category>> expectedResp = new ResponseResult<>(expectedCategories);
        assertEquals(expectedResp.jsonToString(), stringWriter.toString());
    }

    @Test
    public void doGetByUserIdNotExistent() throws IOException {
        long userIdToLook = USERS.size() + 1;
        Collection<Category> expectedCategories = Collections.emptyList();
        Map<String, String[]> parameterMap = Map.of("userId", new String[]{String.valueOf(userIdToLook)});
        when(request.getParameterMap()).thenReturn(parameterMap);

        categoriesServlet.doGet(request, response);

        ResponseResult<Collection<Category>> expectedResp = new ResponseResult<>(expectedCategories);
        assertEquals(expectedResp.jsonToString(), stringWriter.toString());
    }

    @Test
    public void doGetByUserIdNoCategories() throws IOException {
        long userIdToLook = USERS.size();
        Collection<Category> expectedCategories = Collections.emptyList();
        Map<String, String[]> parameterMap = Map.of("userId", new String[]{String.valueOf(userIdToLook)});
        when(request.getParameterMap()).thenReturn(parameterMap);

        categoriesServlet.doGet(request, response);

        ResponseResult<Collection<Category>> expectedResp = new ResponseResult<>(expectedCategories);
        assertEquals(expectedResp.jsonToString(), stringWriter.toString());
    }

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