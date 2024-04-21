package com.solovev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solovev.dto.ResponseResult;
import com.solovev.model.Card;
import com.solovev.model.Category;
import com.solovev.model.User;
import com.solovev.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CardController.class)
@AutoConfigureMockMvc
class CardControllerTest {
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CardService cardService;

    @Test
    public void deleteHappyPass() throws Exception {
        User someUser = new User(1,"log","pass","name");
        Category someCategory = new Category("categoryName",someUser);
        Card someCard = new Card("question","answer",someCategory);

        ResponseResult<Card> expected = new ResponseResult<>(null, someCard);
        when(cardService.deleteById(1)).thenReturn(someCard);

        mockMvc
                .perform(delete("/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}