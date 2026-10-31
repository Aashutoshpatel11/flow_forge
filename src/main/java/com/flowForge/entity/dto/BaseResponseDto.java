package com.flowForge.entity.dto;

import com.flowForge.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class BaseResponseDto {
    private Status responseStatus;
    private String message;
    private List<String> errors = new ArrayList<>();

    public BaseResponseDto(Status responseStatus, String message, String error) {
        this.responseStatus = responseStatus;
        this.message = message;
        this.errors.add(error);
    }
}
