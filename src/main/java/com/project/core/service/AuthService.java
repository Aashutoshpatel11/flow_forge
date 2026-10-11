package com.project.core.service;

import com.project.core.entity.dto.UserLoginRequestDto;
import com.project.core.entity.dto.UserLoginResponseDto;
import com.project.core.entity.dto.UserRegistrationRequestDto;
import com.project.core.entity.model.User;
import com.project.core.enums.Role;
import com.project.core.enums.TokenType;
import com.project.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Setter
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;

    public String registerUser(UserRegistrationRequestDto userDto){
        User newUser = new User();
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());

        newUser.addRole( roleService.getRoleByName(Role.BASIC_USER) );
        newUser.addRole( roleService.getRoleByName(Role.ADVANCE_USER) );

        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(newUser);
        return "User Registered Successfully";
    }

    public ResponseEntity<UserLoginResponseDto> loginUser(UserLoginRequestDto userDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getEmail(),
                        userDto.getPassword()
                )
        );

        User user = userRepository.getUserByEmail(userDto.getEmail());

        UserLoginResponseDto response = new UserLoginResponseDto();

        String accessToken = jwtService.generateToken(user, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.generateToken(user, TokenType.REFRESH_TOKEN);

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        ResponseCookie cookie = ResponseCookie.from("access-token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60*60*24*4)
                .build();

        response.setToken(accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    public User findByEmail(String email){
        return userRepository.getUserByEmail(email);
    }

}
