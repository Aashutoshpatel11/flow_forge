package com.flowForge.controller;

import com.flowForge.entity.dto.UserLoginRequestDto;
import com.flowForge.entity.dto.UserLoginResponseDto;
import com.flowForge.entity.dto.UserRegistrationRequestDto;
import com.flowForge.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/logout")
    public ResponseEntity<String> logout(){
        authService.logout();
        ResponseCookie cookie = ResponseCookie
                .from("access-token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(60*60*24*4)
                .build();

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged Out Successfully");
    }

}
