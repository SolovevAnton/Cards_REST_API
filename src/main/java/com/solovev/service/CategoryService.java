package com.solovev.service;

import com.solovev.model.Category;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
@Transactional
public interface CategoryService {
    Collection<Category> findByUserId(long userId);
    Category findById(long id);
}
