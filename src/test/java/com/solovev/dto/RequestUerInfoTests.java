package com.solovev.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RequestUerInfoTests {
    private ObjectMapper mapper = new ObjectMapper();
    private RequestUserInfo info = new RequestUserInfo("login", "pass");

    @Test
    public void deserializationTest() throws JsonProcessingException {
        String json = "{\"login\":\"login\",\"pass\":\"pass\"}";
        assertEquals(info,mapper.readValue(json,RequestUserInfo.class));
    }

    @Test
    public void serializationTest() throws JsonProcessingException {
        assertEquals("{\"login\":\"login\",\"pass\":\"pass\"}", mapper.writeValueAsString(info));
    }
}
