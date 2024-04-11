package com.solovev.service.imp;

import com.solovev.exception.MyConstraintViolationException;
import com.solovev.model.User;
import com.solovev.repository.UserRepository;
import com.solovev.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
    private final String thisLoginExists = "User with this login already exists";
    private final UserRepository userRepository;

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> find(long id) {
        return userRepository.findById(id); //to do refactor error
    }

    @Override
    public Optional<User> find(String login, String passHash) {
        return userRepository.findUserByLoginAndPassword(login, passHash);
    }

    @Override
    public Optional<User> getUserByCookieHashAndId(String hash, long id) {
        return userRepository.findUserByCookieHashAndId(hash, id);
    }

    @Override
    public void update(User user) {
        userRepository.saveAndFlush(user);
    }

    @Override
    public User tryToAddUser(User toAdd) {
        userRepository
                .findByLogin(toAdd.getLogin())
                .ifPresent((u) -> {
                    throw new MyConstraintViolationException(thisLoginExists);
                });
        return userRepository.save(toAdd);
    }
}

