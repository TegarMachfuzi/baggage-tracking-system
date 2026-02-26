package com.passenger.exception;

import com.baggage.dto.response.ResponseModel;
import com.baggage.util.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PassengerNotFoundException.class)
    public ResponseEntity<ResponseModel> handleNotFound(PassengerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ResponseUtil.error("94", ex.getMessage()));
    }

    @ExceptionHandler(DuplicatePassengerException.class)
    public ResponseEntity<ResponseModel> handleDuplicate(DuplicatePassengerException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ResponseUtil.error("95", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseModel> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseUtil.error("93", errors.toString()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseModel> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ResponseUtil.error("96", "Error in execution"));
    }
}
