package com.empmanagement.controller;

import com.empmanagement.common.DataHandleException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataHandleException.class)
    public ResponseEntity<Map<String, String>> handleNullException(DataHandleException e) {
        Map<String, String> result = new HashMap<>();
        result.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(result);
    }

}
