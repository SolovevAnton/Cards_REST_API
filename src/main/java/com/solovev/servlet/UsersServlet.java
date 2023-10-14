package com.solovev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.dao.daoImplementations.UserDao;
import com.solovev.dto.ResponseResult;
import com.solovev.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

@WebServlet("/users")
public class UsersServlet extends HttpServlet {
    private ResponseResult<User> responseResult;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final UserDao REPO = new UserDao();

    // made not private for tests
    String notFoundIdMessage(String id) {
        return "Cannot find object with this ID: " + id;
    }

    /**
     * gets one or all users based on the given args;<br>
     * one user if in args there are only users id or log and pass;<br>
     * returns all users if id is not present;<br>
     * if nothing found returns response result with the message<br>
     * all returns are in the form of response result object in json format
     *
     * @param req  request to process
     * @param resp response to give
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncodingAndResponseType(req, resp);
        String stringId = req.getParameter("id");

        if (stringId != null) {
            try {
                long id = Long.parseLong(stringId);
                Optional<User> userToReturn = REPO.get(id);

                userToReturn.ifPresentOrElse(user -> responseResult.setData(user),
                        () -> responseResult.setMessage(notFoundIdMessage(stringId)));
            } catch (NumberFormatException e) {
                responseResult.setMessage("Error: " + e);
            }
            resp.getWriter().write(responseResult.jsonToString());
        } else {
            ResponseResult<Collection<User>> resultWithCollection = new ResponseResult<>();
            resultWithCollection.setData(REPO.get());
            resp.getWriter().write(resultWithCollection.jsonToString());
        }
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
        responseResult = new ResponseResult<>();
    }

    /**
     * Checks if request contains json or not
     *
     * @param req to check
     * @return true if is json false otherwise
     */
    private boolean isJson(HttpServletRequest req) {
        String header = req.getHeader("Content-Type");
        return header != null && header.contains("application/json");
    }
}
