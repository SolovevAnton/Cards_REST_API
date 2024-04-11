package com.solovev.controller;

import com.solovev.dto.RequestUserInfo;
import com.solovev.exception.DataNotFoundException;
import com.solovev.exception.MyConstraintViolationException;
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

import java.util.Objects;

@RestController
@RequestMapping("authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final String USER_ID_COOKIE_NAME = "id";
    private static final String HASH_COOKIE_NAME = "hash";

    private final String notFoundMsg = "No user with this login password pair found";
    private final String userNotFoundIdMsg = "User not found with id: ";

    private final int cookieMaxAgeMinutes = 30;
    private final UserService userService;

    @PutMapping
    public ResponseEntity<?> checkUser(@RequestBody RequestUserInfo info) {
        String hashPass = PassHashed.hash(info.pass());
        User foundUser =
                userService.find(info.login(), hashPass).orElseThrow(() -> new DataNotFoundException(notFoundMsg));
        return setCookiesAndUserHash(foundUser, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody User user) {
        User addedUser = userService.tryToAddUser(user);
        return setCookiesAndUserHash(addedUser, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<?>  clearCookies(@CookieValue(name = USER_ID_COOKIE_NAME) long userId) {
        User foundUser = userService.find(userId).orElseThrow(() -> new DataNotFoundException(userNotFoundIdMsg + userId));
        return deleteCookieAndUserHash(foundUser);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<?> handle(DataNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(MyConstraintViolationException.class)
    public ResponseEntity<?> handle(MyConstraintViolationException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(e.getMessage());
    }

    private ResponseEntity<?> deleteCookieAndUserHash(User user) {
        return setCookiesAndUserHash(user,null, HttpStatus.OK);
    }

    private ResponseEntity<?> setCookiesAndUserHash(User user, HttpStatus status) {
        return setCookiesAndUserHash(user, StringUtil.generateHash(),status);
    }

    private ResponseEntity<?> setCookiesAndUserHash(User user, String hash, HttpStatus status) {
        setHashAndUpdateUser(user, hash);

        ResponseCookie idCookie = configureCookie(USER_ID_COOKIE_NAME, user.getId());
        ResponseCookie hashCookie = configureCookie(HASH_COOKIE_NAME, hash);

        return ResponseEntity
                .status(status)
                .header(HttpHeaders.SET_COOKIE, idCookie.toString())
                .header(HttpHeaders.SET_COOKIE, hashCookie.toString())
                .build();
    }

    private void setHashAndUpdateUser(User user, String hash) {
        user.setCookieHash(hash);
        userService.update(user);
    }

    private <T> ResponseCookie configureCookie(String key, T data) {
        String dataValue = Objects.nonNull(data) ? data.toString() : null;
        return ResponseCookie
                .from(key, dataValue)
                .maxAge(cookieMaxAgeMinutes * 60)
                .path("/")
                .build();
    }
}
