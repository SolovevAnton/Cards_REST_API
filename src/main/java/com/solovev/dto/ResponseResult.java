package com.solovev.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class used to get results from server
 * @param <T>
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> {
    private String message;
    private T data;

    public ResponseResult(String message) {
        this.message = message;
    }

    public ResponseResult(T data) {
        this.data = data;
    }

    /**
     * Represents object as a Json sting
     * @return string in Json format of this object
     */
    public String jsonToString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        return objectMapper.writeValueAsString(this);
    }
}
