package com.solovev.repository;

import com.solovev.model.Card;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DataJpaTest
@AutoConfigureTestDatabase
@Sql(scripts = "/reload.sql")
class CardRepositoryTest {
    @Autowired
    private CardRepository cardRepository;

    @Test
    public void testFindByUserId() {
        Collection<Card> expected =
                cardRepository.findAll().stream().filter(c -> c.getCategory().getUser().getId() == 1).toList();
        assumeFalse(expected.isEmpty());

        var result = cardRepository.findAllByCategory_UserId(1);

        assertEquals(expected, result);
    }
    @Test
    public void testFindByUserIdNotFoundUser() {

        var result = cardRepository.findAllByCategory_UserId(100);

        assertEquals(Collections.emptyList(), result);
    }

}