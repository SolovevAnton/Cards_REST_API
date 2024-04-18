package com.solovev.service;

import com.solovev.model.Category;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

public interface CategoryService {
    Collection<Category> findByUserId(long userId);

    Category get(long id);

    Category add(long userId, Category category);
    Category update(Category category);

    void deleteById(long categoryId);
}
