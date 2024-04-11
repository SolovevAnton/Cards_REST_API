package com.solovev.service;

import com.solovev.exception.MyConstraintViolationException;
import com.solovev.model.Category;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Transactional
public interface CategoryService {
    Collection<Category> findByUserId(long userId);
    Optional<Category> findById(long id);
    Category tryToSaveCategory(Category category) throws MyConstraintViolationException;
    void deleteById(long categoryId);
}
