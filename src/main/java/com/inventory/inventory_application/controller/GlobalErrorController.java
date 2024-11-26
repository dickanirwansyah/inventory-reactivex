package com.inventory.inventory_application.controller;

import com.inventory.inventory_application.exception.ResourceNotfoundException;
import com.inventory.inventory_application.model.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(ResourceNotfoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotfound(ResourceNotfoundException e){
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failed(e.getErrors(), e.getStatus()));
    }
}
