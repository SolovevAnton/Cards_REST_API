package com.solovev.service.imp;

import com.solovev.model.Category;
import com.solovev.repository.CategoryRepository;
import com.solovev.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public Collection<Category> findByUserId(long userId) {
        return categoryRepository.findCategoriesByUserId(userId);
    }

    @Override
    public Category findById(long id) {
        return categoryRepository.findById(id).get();
    }
}
