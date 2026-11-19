package com.flowForge.entity.dto;


import com.flowForge.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ErrorResponseDto extends BaseResponseDto{
    public ErrorResponseDto(Status status, String message, String error) {
        super(status, message, error);
    }
    public ErrorResponseDto(Status status, String message, List<String> error) {
        super(status, message, error);
    }
}
