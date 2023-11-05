package com.solovev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.dto.RequestUserInfo;
import com.solovev.model.User;
import com.solovev.util.StringUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static java.util.Objects.nonNull;

@WebServlet("/authentication")
public class AuthenticationServlet extends HttpServlet {
    private final int cookieMaxAgeMinutes = 30;
    private final UserDao userDao = new UserDao();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String onlyJsonRequestsMsg = "only json requests";

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws  IOException {
        configEncoding(req, resp);
        if (isJson(req)) {
            RequestUserInfo info = objectMapper.readValue(req.getReader(), RequestUserInfo.class);
            Optional<User> foundUser = userDao.getUserByLoginAndPass(info.login(), info.pass());
            if (foundUser.isPresent()) {
                setCookiesAndUserHash(foundUser.get(), resp);
                resp.setStatus(200);
            } else {
                resp.setStatus(404);
                resp.getWriter().write("No user with this login password pair found");
            }
        } else {
            resp.setStatus(400);
            resp.getWriter().write(onlyJsonRequestsMsg);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncoding(req, resp);
        if (isJson(req)) {
            User userToAdd = objectMapper.readValue(req.getReader(), User.class);
            try {
                userDao.add(userToAdd);
                setCookiesAndUserHash(userToAdd, resp);
                resp.setStatus(201);
            } catch (IllegalArgumentException e) {
                resp.setStatus(400);
                resp.getWriter().write("User already exists");
            }
        } else {
            resp.setStatus(400);
            resp.getWriter().write(onlyJsonRequestsMsg);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncoding(req, resp);
        Cookie[] cookies = req.getCookies();
        if (nonNull(cookies)) {
            clearUserCookies(cookies);
            clearBrowserCookies(cookies, resp);
        }
    }

    private void clearUserCookies(Cookie[] cookies) {
        Optional<User> foundUser = defineUser(cookies);
        foundUser.ifPresent(user -> {
            user.setCookieHash(null);
            userDao.update(user);
        });
    }

    private Optional<User> defineUser(Cookie[] cookies) {
        String id = null;
        String hash = null;
        for (Cookie cookie : cookies) {
            switch (cookie.getName()) {
                case "id" -> id = cookie.getValue();
                case "hash" -> hash = cookie.getValue();
            }
        }
        return nonNull(id) && nonNull(hash)
                ? userDao.getUserByHashAndId(id, hash)
                : Optional.empty();
    }

    private void clearBrowserCookies(Cookie[] cookies, HttpServletResponse resp) {
        for (Cookie cookie : cookies) {
            cookie.setValue(null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            resp.addCookie(cookie);
        }
    }

    private void setCookiesAndUserHash(User user, HttpServletResponse resp) {
        String hash = StringUtil.generateHash();
        setHashAndUpdateUser(user, hash);

        Cookie idCookie = configureCookie(new Cookie("id", String.valueOf(user.getId())));
        Cookie hashCookie = configureCookie(new Cookie("hash", hash));

        resp.addCookie(idCookie);
        resp.addCookie(hashCookie);
    }

    private void setHashAndUpdateUser(User user, String hash) {
        user.setCookieHash(hash);
        userDao.update(user);
    }

    private Cookie configureCookie(Cookie cookie) {
        cookie.setMaxAge(cookieMaxAgeMinutes * 60);
        cookie.setPath("/");
        return cookie;
    }

    /**
     * Configs resp and req to use UTF-8, also reloads repository
     *
     * @param req  to config
     * @param resp ro config
     */
    private void configEncoding(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
    }

    private boolean isJson(HttpServletRequest req) {
        String header = req.getHeader("Content-Type");
        return header != null && header.contains("application/json");
    }
}
