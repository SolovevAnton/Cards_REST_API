package com.solovev.service;

import com.solovev.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
@Transactional
public interface UserService {
    Collection<User> findAll();
    User find(long id);
    Optional<User> find(String login, String passHash);
    Optional<User> getUserByCookieHashAndId(String hash, long id);
    void update(User user);
}
