package com.solovev.util;

import com.solovev.dao.DAO;
import com.solovev.dto.DTO;
import com.solovev.dto.ResponseResult;

import java.util.Map;
import java.util.Optional;

public class StrategyGetById<T extends DTO> extends StrategyGet<T> {
    private final DAO<T> dao;

    public StrategyGetById(Map<String, String[]> parametersMap, DAO<T> dao) {
        super(parametersMap);
        this.dao = dao;
    }

    @Override
    public ResponseResult<T> getResult() {
        try{
            String idInput = getOneValue("id");
            long id = Long.parseLong(idInput);
            Optional<T> foundObject = dao.get(id);
            configureResponseResult(foundObject, "Cannot find object with this ID: " + idInput);
        } catch (IllegalArgumentException e){
            responseResult.setMessage(e.toString());
        }
        return responseResult;
    }
}
