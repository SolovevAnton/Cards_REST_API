package com.solovev.filter;

import com.solovev.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements Filter {
    private final UserService userService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String authorisationServletURI = "authentication";
    private String additionalJS ="jQuery.min.js";

    @Getter
    private String logInURI = "signIn.html";
    @Getter
    private String registerURI = "registration.html";
    @Getter
    private String redirectURI = "/signIn.html";
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        request = (HttpServletRequest) servletRequest;
        response  = (HttpServletResponse) servletResponse;

        if(isAuthorizedPage() || isAuthorized()){
            filterChain.doFilter(request,response);
        } else {
            response.sendRedirect(request.getContextPath() + redirectURI);
        }

    }
    private boolean isAuthorizedPage(){
        String requestURI = request.getRequestURI();
        List<String> authorizedPages = List.of(
                logInURI,
                registerURI,
                authorisationServletURI,
                additionalJS,
                "authentication.js",
                "authentication.css");
        return authorizedPages.stream().anyMatch(requestURI::endsWith);
    }
    private boolean isAuthorized(){
        Cookie[] cookies = request.getCookies();
        return nonNull(cookies)
                && isAuthorized(cookies);
    }
    private boolean isAuthorized(Cookie[] cookies){
        boolean isAuthorized = false;
        String id = null;
        String hash = null;

        for(Cookie cookie : cookies){
            switch (cookie.getName()){
                case "id" -> id = cookie.getValue();
                case "hash" -> hash = cookie.getValue();
            }
        }

        if(nonNull(id) && nonNull(hash)){
            try {
                long numericId = Long.parseLong(id);
                isAuthorized = userService.getUserByCookieHashAndId(hash,numericId).isPresent();
            } catch (NumberFormatException ignored){
            }
        }
        return isAuthorized;
    }
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}

