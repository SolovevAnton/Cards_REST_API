package com.solovev.filter;

import com.solovev.dao.daoImplementations.UserDao;
import lombok.Getter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;


@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    private HttpServletRequest request;
    private HttpServletResponse response;
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
        List<String> authorizedPages = List.of(logInURI,registerURI,"authentication.js","authentication.css");
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
            isAuthorized = new UserDao().getUserByHashAndId(id,hash).isPresent();
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
