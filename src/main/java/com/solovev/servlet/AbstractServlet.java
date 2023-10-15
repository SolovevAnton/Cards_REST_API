package com.solovev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.dao.DAO;
import com.solovev.dto.DTO;
import com.solovev.dto.ResponseResult;
import com.solovev.util.StrategyGet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

abstract public class AbstractServlet<T extends DTO> extends HttpServlet {
    private final String notFoundMsg = "Object with this params wasn't found";
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    private final Class<T> self;
    private final DAO<T> DAO;
    private ResponseResult<T> responseResult;

    public AbstractServlet(Class<T> self, DAO<T> DAO) {
        super();
        this.self = self;
        this.DAO = DAO;
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
        Map<String,String[]> parameters = req.getParameterMap();

        if (parameters.isEmpty()) {
            ResponseResult<Collection<T>> resultWithCollection = new ResponseResult<>();
            resultWithCollection.setData(DAO.get());
            resp.getWriter().write(resultWithCollection.jsonToString());
        } else {
            StrategyGet<T> strategyGet = defineGetStrategy(parameters);
            Optional<T> elem = strategyGet.getObject(parameters);
            elem.ifPresentOrElse(o -> responseResult.setData(o),
                    () -> responseResult.setMessage(notFoundMsg));
            resp.getWriter().write(responseResult.jsonToString());
        }

    }
    abstract protected StrategyGet<T> defineGetStrategy(Map<String,String[]> parametersMap);


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

    public String getNotFoundMsg() {
        return notFoundMsg;
    }
}
