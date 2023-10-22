package com.solovev.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.dao.DAO;
import com.solovev.dto.DTO;
import com.solovev.dto.ResponseResult;
import com.solovev.util.strategyGet.StrategyGet;
import lombok.Getter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

abstract public class AbstractServlet<T extends DTO> extends HttpServlet {
    @Getter
    private final String messageNoId = "Please provide object ID";
    @Getter
    private final String constrainViolatedMsg = "Object violates DB constraint";
    @Getter
    private final String noStrategyFoundMsg = "Cannot do get request with this params";
    @Getter
    private final String noJsonObjectProvidedMsg = "Please provide JSON object to post";
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    @Getter
    private final DAO<T> dao;
    private final Class<T> self;
    private ResponseResult<T> responseResult;

    public AbstractServlet(Class<T> self, DAO<T> dao) {
        super();
        this.self = self;
        this.dao = dao;
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
     * @throws IOException if IO occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncodingAndResponseType(req, resp);

        Map<String, String[]> parameters = req.getParameterMap();
        Optional<StrategyGet<?>> strategyGet = defineStrategyOfGet(parameters);
        ResponseResult<?> result;
        if (strategyGet.isPresent()) {
            result = strategyGet.get().getResult();
        } else {
            result = new ResponseResult<>();
            result.setMessage(noStrategyFoundMsg);
        }
        writeResponseAndSetCode(resp,result);
    }

    abstract protected Optional<StrategyGet<?>> defineStrategyOfGet(Map<String, String[]> parametersMap);

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
            try {
                long id = Long.parseLong(stringId);
                Optional<T> foundObject = dao.delete(id);

                foundObject.ifPresentOrElse(
                        object -> responseResult.setData(object),
                        () -> responseResult.setMessage(getNotFoundIdMsg(id)));

            } catch (NumberFormatException e) {
                responseResult.setMessage(e.toString());
            }
        } else {
            responseResult.setMessage(messageNoId);
        }

        writeResponseAndSetCode(resp,responseResult);
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

                Optional<T> oldObject = dao.get(updateId);
                if (oldObject.isPresent()) {
                    dao.update(objectReplacement);
                    responseResult.setData(oldObject.get());
                } else {
                    responseResult.setMessage(getNotFoundIdMsg(updateId));
                }
            } catch (IllegalArgumentException e) {
                responseResult.setMessage(constrainViolatedMsg);
            }

            writeResponseAndSetCode(resp,responseResult);
        }
    }

    /**
     * @param req  must contain json object to add
     * @param resp response with posted object if success with message otherwise
     * @throws IOException if IO exc occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configEncodingAndResponseType(req, resp);
        if (isJson(req)) {
            try {
                T objectToAdd = objectMapper.readValue(req.getReader(), self);
                dao.add(objectToAdd);
                responseResult.setData(objectToAdd);
            } catch (IllegalArgumentException e) {
                responseResult.setMessage(constrainViolatedMsg);
            }
        } else {
            responseResult.setMessage(noJsonObjectProvidedMsg);
        }
        writeResponseAndSetCode(resp,responseResult);
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
    private void writeResponseAndSetCode(HttpServletResponse resp,ResponseResult<?> result) throws IOException {
        resp.getWriter().write(result.jsonToString());
        resp.setStatus(result.getMessage() == null ? 200 : 400);
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

}
