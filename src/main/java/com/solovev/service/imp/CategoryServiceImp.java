package com.solovev.service.imp;

import com.solovev.dto.ResponseResult;
import com.solovev.exception.MyConstraintViolationException;
import com.solovev.model.Category;
import com.solovev.repository.CategoryRepository;
import com.solovev.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final String categoryWithThisNameAndUserExists = "Category with name %s and user with id %d already exists";
    private final CategoryRepository categoryRepository;
    @Override
    public Collection<Category> findByUserId(long userId) {
        return categoryRepository.findCategoriesByUserId(userId);
    }
    @Override
    public Optional<Category> findById(long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public Category tryToSaveCategory(Category category) throws MyConstraintViolationException {
        try {
            return categoryRepository.save(category);
        } catch (DataAccessException e){
            throw new MyConstraintViolationException(exceptionMessage(category));
        }
    }

    @Override
    public void deleteById(long categoryId){
        categoryRepository.deleteById(categoryId);
    }


    private String exceptionMessage(Category category){
        return String.format(categoryWithThisNameAndUserExists, category.getName(), category.getUser().getId());
    }
}
