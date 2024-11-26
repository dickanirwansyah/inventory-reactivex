package com.inventory.inventory_application.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    private String message;
    private Integer status;
    private Object data;
    private List<String> errors;

    public static ApiResponse success(Object data){
        return ApiResponse.builder()
                .data(data)
                .message("OK")
                .status(HttpStatus.OK.value())
                .errors(null)
                .build();
    }

    public static ApiResponse failed(List<String> errors, Integer status){
        return ApiResponse.builder()
                .data(null)
                .message("Failed")
                .status(status)
                .errors(errors)
                .build();
    }
}
