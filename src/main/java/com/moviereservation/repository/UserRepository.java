package com.moviereservation.repository;

import com.moviereservation.model.User;
import com.moviereservation.util.JsonFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class UserRepository {
    
    @Autowired
    private JsonFileUtil<User> userJsonFileUtil;
    
    public List<User> findAll() {
        return userJsonFileUtil.readFromFile();
    }
    
    public Optional<User> findById(Long id) {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
    
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
    
    public User save(User user) {
        List<User> users = findAll();
        
        // If new user, generate ID
        if (user.getId() == null) {
            Long maxId = users.stream()
                    .mapToLong(User::getId)
                    .max()
                    .orElse(0L);
            user.setId(maxId + 1);
            users.add(user);
        } else {
            // Update existing user
            users = users.stream()
                    .map(existingUser -> existingUser.getId().equals(user.getId()) ? user : existingUser)
                    .collect(Collectors.toList());
        }
        
        userJsonFileUtil.writeToFile(users);
        return user;
    }
    
    public void deleteById(Long id) {
        List<User> users = findAll();
        users = users.stream()
                .filter(user -> !user.getId().equals(id))
                .collect(Collectors.toList());
        userJsonFileUtil.writeToFile(users);
    }
}
