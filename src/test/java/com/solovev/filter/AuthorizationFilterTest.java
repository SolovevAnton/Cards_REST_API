package com.solovev.filter;

import com.solovev.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationFilterTest {

/*    @Nested
    public class SuccessfulFilter {
        @Test
        public void signInPage() throws ServletException, IOException {
            when(request.getRequestURI()).thenReturn(authorizationFilter.getLogInURI());

            authorizationFilter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        public void registerPage() throws ServletException, IOException {
            when(request.getRequestURI()).thenReturn(authorizationFilter.getRegisterURI());

            authorizationFilter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }

        @Test
        public void foundCookies() throws ServletException, IOException {
            User userToFind = USERS.get(0);
            Cookie[] cookies = new Cookie[]{new Cookie("id", String.valueOf(userToFind.getId())), new Cookie("hash", userToFind.getCookieHash())};
            when(request.getRequestURI()).thenReturn("");
            when(request.getCookies()).thenReturn(cookies);

            authorizationFilter.doFilter(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    public class UnSuccessfulFilter {
        @Test
        public void noCookiesOtherPage() throws ServletException, IOException {
            when(request.getRequestURI()).thenReturn("");
            when(request.getContextPath()).thenReturn(contextPass);

            authorizationFilter.doFilter(request, response, filterChain);

            verify(response).sendRedirect(contextPass + authorizationFilter.getRedirectURI());
        }

        @Test
        public void wrongIdCookies() throws ServletException, IOException {
            User userToNotFind = USERS.get(0);
            Cookie[] cookies = new Cookie[]{new Cookie("id", String.valueOf(userToNotFind.getId() + 1)), new Cookie("hash", userToNotFind.getCookieHash())};
            when(request.getRequestURI()).thenReturn("");
            when(request.getContextPath()).thenReturn(contextPass);

            authorizationFilter.doFilter(request, response, filterChain);

            verify(response).sendRedirect(contextPass + authorizationFilter.getRedirectURI());
        }

        @Test
        public void wrongHashCookies() throws ServletException, IOException {
            User userToNotFind = USERS.get(0);
            Cookie[] cookies = new Cookie[]{new Cookie("id", String.valueOf(userToNotFind.getId())), new Cookie("hash", userToNotFind.getCookieHash() + " corrupted")};
            when(request.getRequestURI()).thenReturn("");
            when(request.getContextPath()).thenReturn(contextPass);

            authorizationFilter.doFilter(request, response, filterChain);

            verify(response).sendRedirect(contextPass + authorizationFilter.getRedirectURI());
        }
    }

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    private AuthorizationFilter authorizationFilter = new AuthorizationFilter();
    private String contextPass = "http://localhost:8080/Cards_REST_API";
    private static final DBSetUpAndTearDown dbSetUpAndTearDown = new DBSetUpAndTearDown();

    private static final List<User> USERS = List.of(
            new User(1, "firstLog", "firstPass", "first", "hashValue")
    );

    @BeforeAll
    public static void setUp() throws SQLException, IOException, ClassNotFoundException {
        dbSetUpAndTearDown.dbFactoryAndTablesCreation();
        dbSetUpAndTearDown.setUpUsersTableValues(USERS);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        dbSetUpAndTearDown.dbFactoryAndTablesTearDown();
    }*/
}