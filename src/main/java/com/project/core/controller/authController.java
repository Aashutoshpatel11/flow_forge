package com.project.core.controller;

import com.project.core.entity.dto.UserLoginRequestDto;
import com.project.core.entity.dto.UserLoginResponseDto;
import com.project.core.entity.dto.UserRegistrationRequestDto;
import com.project.core.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class authController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationRequestDto userDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registerUser(userDto));
    }

    @PostMapping("/login")
    public  ResponseEntity<UserLoginResponseDto> login(@Valid @RequestBody UserLoginRequestDto userDto){
        return authService.loginUser(userDto);
    }
}
