package com.project.security.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequestDto {

    @Size(min = 1, message = "name should be present")
    private String name;

    @Email(message = "invalid email")
    private String email;

    @Size(min = 8, message = "password should be more than 8 character")
    private String password;
}
