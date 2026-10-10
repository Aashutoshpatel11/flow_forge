package com.project.security.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class UserLoginResponseDto {
    private String token;
    private List<String> errors = new ArrayList<>();

    public void addError(String err) {
        errors.add(err);
    }
}
