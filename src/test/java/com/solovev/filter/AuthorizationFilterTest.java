package com.solovev.filter;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationFilterTest {

    @Nested
    public class SuccessfulFilter {
        @Test
        public void signInPage() throws ServletException, IOException {
            when(request.getRequestURI()).thenReturn(authorizationFilter.getLogInURI());

            authorizationFilter.doFilter(request,response,filterChain);

            verify(filterChain).doFilter(request,response);
        }
        @Test
        public void registerPage() throws ServletException, IOException {
            when(request.getRequestURI()).thenReturn(authorizationFilter.getRegisterURI());

            authorizationFilter.doFilter(request,response,filterChain);

            verify(filterChain).doFilter(request,response);
        }
    }
    @Nested
    public class UnSuccessfulFilter{
        @Test
        public void noCookiesOtherPage() throws ServletException, IOException {
                when(request.getRequestURI()).thenReturn("");
                when(request.getContextPath()).thenReturn(contextPass);

                authorizationFilter.doFilter(request,response,filterChain);

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
}