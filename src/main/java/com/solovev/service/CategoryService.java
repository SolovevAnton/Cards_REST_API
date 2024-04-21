package com.solovev.service;

import com.solovev.model.Category;

import java.util.Collection;

public interface CategoryService {
    Collection<Category> findByUserId(long userId);

    Category get(long id);

    Category add(long userId, Category category);
    Category update(Category category);
    Category deleteById(long categoryId);
}
