package com.solovev.util;

import com.solovev.dto.ResponseResult;

import java.util.Map;
import java.util.Optional;

public abstract class StrategyGet<T> {
    private final ResponseResult<T> responseResult = new ResponseResult<>();
    private final Map<String,String[]> parametersMap;

    public StrategyGet(Map<String, String[]> parametersMap) {
        this.parametersMap = parametersMap;
    }

    /**
     * Tries to get object,
     *
     * @return response with object or with some error message
     */
    public abstract ResponseResult<T> getResult();
    /**
     * Checks if string contains one key, if it does return is else throws
     * @param key to get
     * @return single key
     * @throws IllegalArgumentException if there is more than one key or none found
     */
    protected String getOneValue(String key) throws IllegalArgumentException{
        String[] values = parametersMap.get(key);
        if(values == null){
            throw new IllegalArgumentException("key not found");
        } else if (values.length > 1) {
            throw new IllegalArgumentException("all values must be unique");
        }
        return values[0];
    }

    /**
     * If present adds to response result, else writes a message there
     */
    protected void configureResponseResult(Optional<T> foundElem, String messageIfNotFound) {
        foundElem.ifPresentOrElse(
                responseResult::setData,
                () -> responseResult.setMessage(messageIfNotFound));
    }

    public ResponseResult<T> getResponseResult() {
        return responseResult;
    }
}
