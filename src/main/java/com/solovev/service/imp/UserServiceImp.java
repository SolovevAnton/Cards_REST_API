package com.solovev.service.imp;

import com.solovev.model.User;
import com.solovev.repository.UserRepository;
import com.solovev.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

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
        return userRepository.findById(id).get(); //to do refactor error
    }

    @Override
    public Optional<User> find(String login, String passHash) {
        return userRepository.findUserByLoginAndPassword(login,passHash);
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
    public boolean tryToAddUser(User toAdd) {
        try{
            userRepository.save(toAdd);
            return true;
        } catch (DataAccessException e){
            throw new IllegalArgumentException();
        }
    }
}

