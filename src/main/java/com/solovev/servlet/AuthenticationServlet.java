package com.solovev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.dto.RequestUserInfo;
import com.solovev.dto.ResponseResult;
import com.solovev.model.User;
import com.solovev.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {
    private final int cookieMaxAgeMinutes = 30;
    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       configEncodingAndResponseType(req,resp);
       if(isJson(req)){
           RequestUserInfo info = new ObjectMapper().readValue(req.getReader(), RequestUserInfo.class);
           Optional<User> foundUser = userDao.getUserByLoginAndPass(info.login(),info.pass());
           if(foundUser.isPresent()){
               setCookiesAndUserHash(foundUser.get(),resp);
           } else {
               resp.setStatus(404);
               resp.getWriter().write("No user with this login password pair found");
           }
       } else {
           resp.setStatus(400);
           resp.getWriter().write("only json requests");
       }
    }
    private void setCookiesAndUserHash(User user, HttpServletResponse resp){
        String hash = StringUtil.generateHash();
        setHashAndUpdateUser(user,hash);

        Cookie idCookie = configureCookie(new Cookie("id",String.valueOf(user.getId())));
        Cookie hashCookie = configureCookie(new Cookie("hash",hash));

        resp.addCookie(idCookie);
        resp.addCookie(hashCookie);
    }
    private void setHashAndUpdateUser(User user,String hash){
        user.setCookieHash(hash);
        userDao.update(user);
    }
    private Cookie configureCookie(Cookie cookie){
        cookie.setMaxAge(cookieMaxAgeMinutes*60);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * Configs resp and req to use UTF-8, also reloads repository
     *
     * @param req  to config
     * @param resp ro config
     */
    private void configEncodingAndResponseType(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json;charset=utf-8");
    }
    private boolean isJson(HttpServletRequest req) {
        String header = req.getHeader("Content-Type");
        return header != null && header.contains("application/json");
    }
}
