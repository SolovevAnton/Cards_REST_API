package com.solovev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.dao.DAO;
import com.solovev.dto.DTO;
import com.solovev.dto.ResponseResult;

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
     * gets one or all objects based on the given args;<br>
     * one object if accessed by id or other params<br>
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
        Map<String, String[]> parameters = req.getParameterMap();

        if (parameters.isEmpty()) {
            ResponseResult<Collection<T>> resultWithCollection = new ResponseResult<>();
            resultWithCollection.setData(DAO.get());
            resp.getWriter().write(resultWithCollection.jsonToString());
        } else {
            Optional<StrategyGet<T>> strategyGet = defineGetStrategy(parameters);
            if (strategyGet.isPresent()) {
                Optional<T> foundElem = strategyGet.get().getObject(parameters);

                foundElem.ifPresentOrElse(
                        object -> responseResult.setData(object),
                        () -> responseResult.setMessage(notFoundMsg));
                resp.getWriter().write(responseResult.jsonToString());
            }
        }
    }

    /**
     * Interface to get object from DB based on some params
     */
    @FunctionalInterface
    protected interface StrategyGet<T> {
        Optional<T> getObject(Map<String, String[]> parametersMap);
    }

    abstract protected Optional<StrategyGet<T>> defineGetStrategy(Map<String, String[]> parametersMap);

    protected StrategyGet<T> getById(Map<String, String[]> parametersMap) {
        return (parameters) -> {
            String[] idString = parametersMap.get("id");
            if (idString.length > 1) {
                throw new IllegalArgumentException("cannot exceed one id in request");
            }
            long id = Long.parseLong(idString[0]);
            return DAO.get(id);
        };
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

    public String getNotFoundMsg() {
        return notFoundMsg;
    }
}
