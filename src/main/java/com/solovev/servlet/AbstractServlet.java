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
    private final DAO<T> DAO;
    private final Class<T> self;
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
                Optional<T> foundElem = strategyGet.get().getObject();

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
        Optional<T> getObject();
    }

    abstract protected Optional<StrategyGet<T>> defineGetStrategy(Map<String, String[]> parametersMap);

    protected StrategyGet<T> getById(Map<String, String[]> parametersMap) {
        return () -> {
            String idString = getOneValue(parametersMap,"id");
            long id = Long.parseLong(idString);
            return DAO.get(id);
        };
    }

    /**
     * Checks if string contains one value, if it does return is else throws
     * @param parametersMap map to get value from
     * @param value to get
     * @return single value
     * @throws IllegalArgumentException if there is more than one value or none found
     */
    protected String getOneValue(Map<String, String[]> parametersMap, String value) throws IllegalArgumentException{
        String[] values = parametersMap.get(value);
        if(values == null){
            throw new IllegalArgumentException("value not found");
        } else if (values.length > 1) {
            throw new IllegalArgumentException("all values must be unique");
        }
        return values[0];
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
