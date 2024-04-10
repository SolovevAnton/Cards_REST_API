package com.solovev.controller;

import com.solovev.dto.ResponseResult;
import com.solovev.model.User;
import com.solovev.service.UserService;
import com.solovev.util.PassHashed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userServiceImp;
    @GetMapping
    public ResponseEntity<ResponseResult<Collection<User>>> getAllUsers(){
        return ResponseEntity.ok(new ResponseResult<>(userServiceImp.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult<User>> getUser(@PathVariable long id){
        return ResponseEntity.ok(new ResponseResult<>(userServiceImp.find(id)));
    }

    @GetMapping
    public ResponseEntity<ResponseResult<User>> getUser(@RequestParam String login, @RequestParam String password){
        String passHashed = PassHashed.hash(password);
        return ResponseEntity.ok(new ResponseResult<>(userServiceImp.find(login,passHashed)));
    }


}
