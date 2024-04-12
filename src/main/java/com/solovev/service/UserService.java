package com.solovev.service;

import com.solovev.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

public interface UserService {
    Collection<User> findAll();

    User find(long id);

    User find(String login, String passHash);

    User add(User toAdd);

    User update(User user);
    User deleteById(long id);
}
