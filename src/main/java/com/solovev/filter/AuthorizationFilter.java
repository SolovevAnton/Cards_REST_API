package com.solovev.filter;

import lombok.Getter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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

        if(isAuthorized() || isLoginOrRegisterPage()){
            filterChain.doFilter(request,response);
        } else {
            response.sendRedirect(request.getContextPath() + redirectURI);
        }

    }
    private boolean isAuthorized(){
        request.getCookies();
        return false;
    }
    private boolean isLoginOrRegisterPage(){
        String requestURI = request.getRequestURI();
        return requestURI.endsWith(logInURI) || requestURI.endsWith(registerURI);
    }
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {

    }
}
