package com.solovev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.solovev.model.User;
import com.solovev.service.UserService;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    private final String api_endpoint = "/users";
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void shouldReturnUserByExistingId() throws Exception {
        User existingUser = new User();
        int existingId = 1;
        existingUser.setId(existingId);
        existingUser.setName("user");
        existingUser.setLogin("userLogin");

        when(userService.find(existingId)).thenReturn(existingUser);
        //then
        mockMvc.perform(get(api_endpoint + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id", is(existingId)))
                .andExpect(jsonPath("$.data.name", is("user")));
    }
    @Test
    public void shouldReturnErrorByCallingNotExistingID() throws Exception {
        int nonExistingId = 2;
        String message = "message should be shown";
        when(userService.find(nonExistingId)).thenThrow(new IllegalArgumentException(message));
        //then
        mockMvc.perform(get(api_endpoint + "/2"))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message", is(message)));
    }
}