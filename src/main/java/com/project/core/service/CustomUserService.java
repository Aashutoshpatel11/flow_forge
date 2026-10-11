package com.project.core.service;

import com.project.core.entity.model.User;
import com.project.core.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class CustomUserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userRepository.getUserByEmail(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .authorities(user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).toList())
                .password(user.getPassword())
                .build();
    }
}
