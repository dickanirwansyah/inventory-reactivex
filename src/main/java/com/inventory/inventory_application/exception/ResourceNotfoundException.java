package com.inventory.inventory_application.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ResourceNotfoundException extends RuntimeException{

    private int status;
    private List<String> errors;

    public ResourceNotfoundException(int status, List<String> errors, String message) {
        super(message);
        this.status = status;
        this.errors = errors;
    }
}
