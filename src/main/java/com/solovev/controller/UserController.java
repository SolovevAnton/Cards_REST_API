package com.solovev.controller;

import com.solovev.dto.ResponseResult;
import com.solovev.model.User;
import com.solovev.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ResponseResult<Collection<User>>> getAllUsers() {
        return ResponseEntity.ok(new ResponseResult<>(null, userService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult<User>> getUser(@PathVariable long id) {
        try {
            User foundUser = userService.find(id);
            return ResponseEntity.ok(new ResponseResult<>(null, foundUser));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(params = {"login", "password"})
    public ResponseEntity<ResponseResult<User>> getUser(@RequestParam String login, @RequestParam String password) {
        try {
            User foundUser = userService.find(login, password);
            return ResponseEntity.ok(new ResponseResult<>(null, foundUser));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping
    public ResponseEntity<ResponseResult<User>> add(@RequestBody User user) {
        try {
            this.userService.add(user);
            return new ResponseEntity<>(new ResponseResult<>(null, user), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null),
                    HttpStatus.CONFLICT);
        }
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ResponseResult<User>> delete(@PathVariable long id) {
        try {
            User user = this.userService.deleteById(id);
            return new ResponseEntity<>(new ResponseResult<>(null, user), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null),
                    HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<ResponseResult<User>> put(@RequestBody User user) {
        try {
            User res = this.userService.update(user);
            return new ResponseEntity<>(new ResponseResult<>(null, res), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new ResponseResult<>(e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
