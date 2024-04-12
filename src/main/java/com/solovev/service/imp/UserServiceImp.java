package com.solovev.service.imp;

import com.mysql.cj.exceptions.DataConversionException;
import com.solovev.model.User;
import com.solovev.repository.UserRepository;
import com.solovev.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User find(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with this ID does not exist"));
    }

    @Override
    public User find(String login, String passHash) {
        return userRepository
                .findUserByLoginAndPassword(login, passHash)
                .orElseThrow(() -> new IllegalArgumentException("User with this Login and pass"));
    }

    @Override
    public User update(User user) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with this login already exists");
        }
    }

    @Override
    public User add(User toAdd) {
        try {
            return userRepository.save(toAdd);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with this login already exists");
        }
    }

    @Transactional
    @Override
    public User deleteById(long id) {
        User foundUser = find(id);
        userRepository.deleteById(id);
        return foundUser;
    }
}

