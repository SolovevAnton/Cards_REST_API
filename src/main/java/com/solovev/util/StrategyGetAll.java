package com.solovev.util;

import com.solovev.dao.DAO;
import com.solovev.dto.DTO;
import com.solovev.dto.ResponseResult;

import java.util.Collection;
import java.util.Map;

public class StrategyGetAll<T extends DTO> extends StrategyGet<Collection<T>> {
    private final DAO<T> dao;

    public StrategyGetAll(Map<String, String[]> parametersMap, DAO<T> dao) {
        super(parametersMap);
        this.dao = dao;
    }

    @Override
    public ResponseResult<Collection<T>> getResult() {
        responseResult.setData(dao.get());
        return responseResult;
    }
}

