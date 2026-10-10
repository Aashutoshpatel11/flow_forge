package com.project.security.controller;

import com.project.security.entity.dto.UserLoginRequestDto;
import com.project.security.entity.dto.UserLoginResponseDto;
import com.project.security.entity.dto.UserRegistrationRequestDto;
import com.project.security.entity.model.User;
import com.project.security.enums.TokenType;
import com.project.security.service.AuthService;
import com.project.security.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/v1/auth")
@RequiredArgsConstructor
public class authController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid UserRegistrationRequestDto userDto){
        User newUser = new User();
        newUser.setName(userDto.getName());
        newUser.setEmail(userDto.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = authService.saveUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body("User Registered Successfully");
    }

    @PostMapping("/login")
    public  ResponseEntity<UserLoginResponseDto> login(@Valid UserLoginRequestDto userDto){
        User existingUser = authService.findByEmail(userDto.getEmail());

        UserLoginResponseDto response = new UserLoginResponseDto();

        if( existingUser==null ){
            response.addError("User doesn't exists with email " +  existingUser.getEmail());
        }
        if( !passwordEncoder.matches(userDto.getPassword(), existingUser.getPassword())){
            response.addError("Wrong Password");
        }

        if (response.getErrors().size()>0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String accessToken = jwtService.generateToken(existingUser, TokenType.ACCESS_TOKEN);
        String refreshToken = jwtService.generateToken(existingUser, TokenType.REFRESH_TOKEN);

        existingUser.setRefreshToken(refreshToken);
        authService.saveUser(existingUser);

        ResponseCookie cookie = ResponseCookie.from("access-token", accessToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60*60)
                .build();

        response.setToken(accessToken);

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

}
