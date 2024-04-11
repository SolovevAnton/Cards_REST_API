package com.solovev.service;

import com.solovev.exception.MyConstraintViolationException;
import com.solovev.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;
@Transactional
public interface UserService {
    Collection<User> findAll();
    Optional<User> find(long id);
    Optional<User> find(String login, String passHash);
    Optional<User> getUserByCookieHashAndId(String hash, long id);
    User tryToAddUser(User toAdd) throws MyConstraintViolationException;
    void update(User user);
}
