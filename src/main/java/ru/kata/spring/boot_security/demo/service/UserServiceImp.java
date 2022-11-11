package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import java.util.List;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
    }
    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void update(User user, Long id) {
        User updateUser = userRepository.findById(id).orElse(null);
        updateUser.setName(user.getName());
        updateUser.setLastName(user.getLastName());
        updateUser.setAbility(user.getAbility());
        updateUser.setAlias(user.getAlias());
        updateUser.setUsername(user.getUsername());
        updateUser.setPassword(user.getPassword());
        updateUser.setRoles(user.getRoles());
        userRepository.saveAndFlush(updateUser);
    }

}
