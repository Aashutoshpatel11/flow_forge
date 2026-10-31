package com.flowForge.config;

import com.flowForge.entity.dto.ErrorResponseDto;
import com.flowForge.enums.Status;
import com.flowForge.exception.FlowForgeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandling {

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e){

        List<String> errors = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach( err ->
            errors.add(err.getDefaultMessage())
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body( new ErrorResponseDto(Status.FAILURE, HttpStatus.BAD_REQUEST.toString() , errors) );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> defaultHandlerExceptionResolverHandler(RuntimeException e){
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body( new ErrorResponseDto(Status.FAILURE, HttpStatus.BAD_REQUEST.toString() , e.getMessage()) );
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDto> flowForgeExceptionHandler(FlowForgeException e){
        return  ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body( new ErrorResponseDto(Status.FAILURE, HttpStatus.BAD_REQUEST.toString() , e.getMessage()) );
    }
//    TODO
//    IllegalArgumentException
//    TODO
//    OptimisticLockingFailureException

}
