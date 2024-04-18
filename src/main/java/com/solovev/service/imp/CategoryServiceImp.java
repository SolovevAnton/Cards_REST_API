package com.solovev.service.imp;

import com.solovev.model.Category;
import com.solovev.model.User;
import com.solovev.repository.CategoryRepository;
import com.solovev.service.CategoryService;
import com.solovev.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {
    private final String categoryWithThisNameAndUserExists = "Category with name %s and user with id %d already exists";
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Override
    public Collection<Category> findByUserId(long userId) {
        return categoryRepository.findCategoriesByUserId(userId);
    }

    @Override
    public Category get(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category with this id wasn't found"));
    }

    @Transactional
    @Override
    public Category add(long userId, Category category) {
        User foundUser = userService.find(userId);
        category.setUser(foundUser);
        try{
           return categoryRepository.save(category);
        } catch (DataIntegrityViolationException e){
            throw new IllegalArgumentException(exceptionMessage(category));
        }
    }

    @Transactional
    @Override
    public Category update(Category category) {
        Category old = this.get(category.getId());
        old.setName(category.getName());
        try{
            return categoryRepository.save(old);
        } catch (Exception e){
            throw new IllegalArgumentException(exceptionMessage(old));
        }
    }

    @Override
    public void deleteById(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }


    private String exceptionMessage(Category category) {
        return String.format(categoryWithThisNameAndUserExists, category.getName(), category.getUser().getId());
    }
}
