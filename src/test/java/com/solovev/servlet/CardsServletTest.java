package com.solovev.servlet;

import com.solovev.DBSetUpAndTearDown;
import com.solovev.DataConstants;
import com.solovev.dto.ResponseResult;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class CardsServletTest {
    @Nested
    public class getByUserId {
        private final String parameterName = "userId";
        @Test
        public void doGetByUserIdSuccess() throws IOException {
            User toFindCardsFor = USERS.get(0);
            long userIdToLook = toFindCardsFor.getId();
            Collection<Card> expectedCards = CARDS.stream().filter(c -> c.getCategory().getUser().equals(toFindCardsFor)).toList();
            Map<String, String[]> parameterMap = Map.of(parameterName, new String[]{String.valueOf(userIdToLook)});
            when(request.getParameterMap()).thenReturn(parameterMap);

            cardsServlet.doGet(request, response);

            ResponseResult<Collection<Card>> expectedResp = new ResponseResult<>(expectedCards);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetByUserIdNotExistent() throws IOException {
            long userIdToLook = USERS.size() + 1;
            Collection<Card> expectedCards = Collections.emptyList();
            Map<String, String[]> parameterMap = Map.of(parameterName, new String[]{String.valueOf(userIdToLook)});
            when(request.getParameterMap()).thenReturn(parameterMap);

            cardsServlet.doGet(request, response);

            ResponseResult<Collection<Card>> expectedResp = new ResponseResult<>(expectedCards);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetByUserIdNoCategories() throws IOException {
            long categoryIdToLook = CATEGORIES.size();
            Collection<Card> expectedCards = Collections.emptyList();
            Map<String, String[]> parameterMap = Map.of(parameterName, new String[]{String.valueOf(categoryIdToLook)});
            when(request.getParameterMap()).thenReturn(parameterMap);

            cardsServlet.doGet(request, response);

            ResponseResult<Collection<Card>> expectedResp = new ResponseResult<>(expectedCards);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }
    }
    @Nested
    public class getByCategory{
        private final String parameterName = "categoryId";
        @Test
        public void doGetByCategoryIdSuccess() throws IOException {
            Category toFindCardsFor = CATEGORIES.get(0);
            long categoryIdToLook = toFindCardsFor.getId();
            Collection<Card> expectedCards = CARDS.stream().filter(c -> c.getCategory().equals(toFindCardsFor)).toList();
            Map<String, String[]> parameterMap = Map.of(parameterName, new String[]{String.valueOf(categoryIdToLook)});
            when(request.getParameterMap()).thenReturn(parameterMap);

            cardsServlet.doGet(request, response);

            ResponseResult<Collection<Card>> expectedResp = new ResponseResult<>(expectedCards);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetByUserIdNotExistent() throws IOException {
            long categoryIdToLook = CATEGORIES.size() + 1;
            Collection<Card> expectedCards = Collections.emptyList();
            Map<String, String[]> parameterMap = Map.of(parameterName, new String[]{String.valueOf(categoryIdToLook)});
            when(request.getParameterMap()).thenReturn(parameterMap);

            cardsServlet.doGet(request, response);

            ResponseResult<Collection<Card>> expectedResp = new ResponseResult<>(expectedCards);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }

        @Test
        public void doGetByUserIdNoCategories() throws IOException {
            long categoryIdToLook = CATEGORIES.size();
            Collection<Card> expectedCards = Collections.emptyList();
            Map<String, String[]> parameterMap = Map.of(parameterName, new String[]{String.valueOf(categoryIdToLook)});
            when(request.getParameterMap()).thenReturn(parameterMap);

            cardsServlet.doGet(request, response);

            ResponseResult<Collection<Card>> expectedResp = new ResponseResult<>(expectedCards);
            assertEquals(expectedResp.jsonToString(), stringWriter.toString());
        }
    }
    private final CardsServlet cardsServlet = new CardsServlet();
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
        dbSetUpAndTearDown.setUpCardsTableValues(CARDS);

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
    private final List<Card> CARDS = DataConstants.CARDS;

}