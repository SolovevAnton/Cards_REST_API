package com.solovev.controller;

import com.solovev.dto.RequestUserInfo;
import com.solovev.model.User;
import com.solovev.service.UserService;
import com.solovev.util.PassHashed;
import com.solovev.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final String notFoundMsg = "No user with this login password pair found";
    private final String thisLoginExists = "User with this login already exists";
    private final int cookieMaxAgeMinutes = 30;
    private final UserService userService;

    @PutMapping
    public ResponseEntity<?> checkUser(@RequestBody RequestUserInfo info) {
        String hashPass = PassHashed.hash(info.pass());
        Optional<User> foundUser = userService.find(info.login(), hashPass);
        return foundUser.isPresent() ? setCookiesAndUserHash(foundUser.get(), HttpStatus.OK) :
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundMsg);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user){
        boolean isAdded = userService.tryToAddUser(user);
        return isAdded
                ? setCookiesAndUserHash(user, HttpStatus.CREATED)
                : ResponseEntity.status(HttpStatus.CONFLICT).body(thisLoginExists);
    }


    private ResponseEntity<?> setCookiesAndUserHash(User user, HttpStatus status) {
        String hash = StringUtil.generateHash();
        setHashAndUpdateUser(user, hash);

        ResponseCookie idCookie = configureCookie("id", user.getId());
        ResponseCookie hashCookie = configureCookie("hash", hash);

        return ResponseEntity
                .status(status)
                .header(HttpHeaders.SET_COOKIE,idCookie.toString())
                .header(HttpHeaders.SET_COOKIE,hashCookie.toString())
                .build();
    }

    private void setHashAndUpdateUser(User user, String hash) {
        user.setCookieHash(hash);
        userService.update(user);
    }

    private <T> ResponseCookie configureCookie(String key, T data) {
        return ResponseCookie
                .from(key, data.toString())
                .maxAge(cookieMaxAgeMinutes * 60)
                .path("/")
                .build();
    }
}
