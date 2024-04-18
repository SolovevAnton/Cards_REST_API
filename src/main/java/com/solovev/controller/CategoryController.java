package com.solovev.controller;

import com.solovev.dto.ResponseResult;
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
    private final CategoryService categoryService;
    @GetMapping( path ="/{userId}")
    public ResponseEntity<ResponseResult<Collection<Category>>> getAll(@PathVariable long userId){
        return ResponseEntity.ok(new ResponseResult<>(null,categoryService.findByUserId(userId)));
    }

    @PostMapping(path = "/{userId}")
    public ResponseEntity<ResponseResult<Category>> add(@PathVariable long userId, @RequestBody Category category) {
        try {
            categoryService.add(userId, category);
            return new ResponseEntity<>(new ResponseResult<>(null, category), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseResult<Category>> update(@RequestBody Category category) {
        try {
            categoryService.update(category);
            return new ResponseEntity<>(new ResponseResult<>(null, category), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
