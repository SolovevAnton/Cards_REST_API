package com.solovev.controller;

import com.solovev.dto.ResponseResult;
import com.solovev.exception.DataNotFoundException;
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
    private final String userNotFoundIdMsg = "User not found with id: ";
    private final String userNotFoundLogAndPassMsg = "No user with this login password pair found";


    private final UserService userServiceImp;
    @GetMapping
    public ResponseEntity<ResponseResult<Collection<User>>> getAllUsers(){
        return ResponseEntity.ok(new ResponseResult<>(userServiceImp.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseResult<User>> getUser(@PathVariable long id){
        User foundUser = userServiceImp.find(id).orElseThrow(() -> new DataNotFoundException(userNotFoundIdMsg + id));
        return ResponseEntity.ok(new ResponseResult<>(foundUser));
    }

    @GetMapping(params = {"login","password"})
    public ResponseEntity<ResponseResult<User>> getUser(@RequestParam String login, @RequestParam String password){
        String passHashed = PassHashed.hash(password);
        User foundUser = userServiceImp.find(login,passHashed).orElseThrow(() -> new DataNotFoundException(userNotFoundLogAndPassMsg));
        return ResponseEntity.ok(new ResponseResult<>(foundUser));
    }


}
