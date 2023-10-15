package com.solovev.util;

import com.solovev.dto.ResponseResult;

import java.util.Optional;

public abstract class GetStrategy<T> {
    private ResponseResult<T> responseResult = new ResponseResult<>();
    private String messageIfNotFound;

    /**
     * Tries to get object,
     *
     * @return response with object or with some error message
     */
    public ResponseResult<T> getResult(String messageIfNotFound) {
        this.messageIfNotFound = messageIfNotFound;
        Optional<T> foundObject = getObject();
        configureResponseResult(foundObject);
        return responseResult;
    }

    protected abstract Optional<T> getObject();

    /**
     * If present adds to response result, else writes a message there
     */
    private void configureResponseResult(Optional<T> foundElem) {
        foundElem.ifPresentOrElse(
                object -> responseResult.setData(object),
                () -> responseResult.setMessage(messageIfNotFound));
    }
}
