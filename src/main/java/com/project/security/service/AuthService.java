package com.project.security.service;

import com.project.security.entity.model.User;
import com.project.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Setter
public class AuthService {
    private final UserRepository userRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }

    public User loginUser(User user){
        return null;
    }

    public User findByEmail(String email){
        return userRepository.getUserByUserName(email);
    }

}
