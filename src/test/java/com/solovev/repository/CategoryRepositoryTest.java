package com.solovev.repository;

import com.solovev.model.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import static org.junit.jupiter.api.Assertions.assertThrows;
@DataJpaTest
@AutoConfigureTestDatabase
@Sql(scripts = "/reload.sql")
class CategoryRepositoryTest {


    @Autowired
    private CategoryRepository repository;

    @Test
    public void saveOnUpdateShouldThrow() {
        Category existing = repository.getById(1L);
        Category categoryToModify = repository.getById(2L);

        categoryToModify.setName(existing.getName());

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(categoryToModify));
    }

    @Test
    public void saveOnAddShouldThrow() {
        Category existing = repository.getById(1L);
        Category otherCategory = new Category(existing.getName(), existing.getUser());

        assertThrows(DataIntegrityViolationException.class,() ->assertThrows(DataIntegrityViolationException.class, () -> repository.save(otherCategory)));
    }
}