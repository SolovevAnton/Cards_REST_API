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
    private final String messageNoId = "Please provide object ID";
    private final String constrainViolatedMsg = "Object violates DB constraint";
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
            Optional<StrategyGet<T>> strategyGet = defineStrategyOfGet(parameters);
            if (strategyGet.isPresent()) {
                ResponseResult<T> result = strategyGet.get().getResult();
                resp.getWriter().write(result.jsonToString());
            }
        }
    }

    abstract protected Optional<StrategyGet<T>> defineStrategyOfGet(Map<String, String[]> parametersMap);

    /**
     * Deletes object from db by id, if not found returns message
     *
     * @param req  should contain id
     * @param resp with response result
     * @throws IOException if IO occurs
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncodingAndResponseType(req, resp);
        String stringId = req.getParameter("id");
        //returns deleted car or throws
        if (stringId != null) {
            long id = Long.parseLong(stringId);
            Optional<T> foundObject = DAO.delete(id);

            foundObject.ifPresentOrElse(
                    object -> responseResult.setData(object),
                    () -> responseResult.setMessage(getNotFoundIdMsg(id)));
        } else {
            responseResult.setMessage(messageNoId);
        }

        resp.getWriter().write(responseResult.jsonToString());
    }

    /**
     * Updates object in the DB based on its id;
     *
     * @param req  request must contain JSON object
     * @param resp response will contain REPLACED object
     * @throws IOException if IO exc occurs
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncodingAndResponseType(req, resp);
        if (isJson(req)) {
            try {
                T objectReplacement = objectMapper.readValue(req.getReader(), self);
                long updateId = objectReplacement.getId();

                Optional<T> oldObject = DAO.get(updateId);
                if (oldObject.isPresent()) {
                    DAO.update(objectReplacement);
                    responseResult.setData(oldObject.get());
                } else {
                    responseResult.setMessage(getNotFoundIdMsg(updateId));
                }
            } catch (IllegalArgumentException e) {
                responseResult.setMessage(constrainViolatedMsg);
            }

            resp.getWriter().write(responseResult.jsonToString());
        }
    }

    /**
     *
     * @param req  must contain json object to add
     * @param resp response with posted object if success with message otherwise
     * @throws IOException if IO exc occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncodingAndResponseType(req, resp);
        if (isJson(req)) {
            try{
                T objectToAdd = objectMapper.readValue(req.getReader(),self);
                DAO.add(objectToAdd);
                responseResult.setData(objectToAdd);
            } catch (IllegalArgumentException e){
                responseResult.setMessage(constrainViolatedMsg);
            }
        }
        resp.getWriter().write(responseResult.jsonToString());
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

    public String getNotFoundIdMsg(long id) {
        return "Cannot find object with this ID: " + id;
    }

    public String getMessageNoId() {
        return messageNoId;
    }

    public String getConstrainViolatedMsg() {
        return constrainViolatedMsg;
    }
}
