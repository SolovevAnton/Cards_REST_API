package com.solovev.controller;

import com.solovev.dto.ResponseResult;
import com.solovev.model.Category;
import com.solovev.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping
    public ResponseEntity<ResponseResult<Collection<Category>>> findCategoriesForUser(@RequestParam long userId){
        return ResponseEntity.ok(new ResponseResult<>(categoryService.findByUserId(userId)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult<Category>> findCategory(@PathVariable long id){
        return ResponseEntity.ok(ResponseResult.of(categoryService.findById(id)));
    }
}
