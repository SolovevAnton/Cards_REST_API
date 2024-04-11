package com.solovev.controller;

import com.solovev.dto.ResponseResult;
import com.solovev.exception.DataNotFoundException;
import com.solovev.model.Category;
import com.solovev.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final String categoryNotFoundIdMsg = "No category exists with id: ";
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseResult<Collection<Category>>> findCategoriesForUser(@RequestParam long userId){
        return ResponseEntity.ok(new ResponseResult<>(categoryService.findByUserId(userId)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult<Category>> findCategory(@PathVariable long id){
        Category foundCategory = categoryService.findById(id).orElseThrow(() -> new DataNotFoundException(categoryNotFoundIdMsg + id));
        return ResponseEntity.ok(ResponseResult.of(foundCategory));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addCategory(@RequestBody Category categoryToAdd){
        categoryService.tryToSaveCategory(categoryToAdd);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void addCategory(@PathVariable long id,@RequestBody Category categoryToAdd){
        categoryToAdd.setId(id);
        categoryService.tryToSaveCategory(categoryToAdd);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(@PathVariable long id){
        categoryService.deleteById(id);
    }
}
