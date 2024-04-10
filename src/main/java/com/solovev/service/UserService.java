package com.solovev.service;

import com.solovev.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    Collection<User> findAll();
    User find(long id);
    User find(String login, String passHash);
    Optional<User> getUserByCookieHashAndId(String hash, long id);

}
