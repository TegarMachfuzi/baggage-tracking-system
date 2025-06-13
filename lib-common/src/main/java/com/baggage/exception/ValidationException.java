package com.baggage.exception;

import com.baggage.constant.RespCodeConstant;
import com.baggage.constant.RespMsgConstant;
import com.baggage.dto.response.ResponseModel;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ValidationException {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseModel> notValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        exception.getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));

        ResponseModel resp = new ResponseModel();
        resp.setResponseCode(RespCodeConstant.RC_96);
        resp.setResponseMessage(RespMsgConstant.FAILED);
        resp.setData(errors);

        return new ResponseEntity<ResponseModel>(resp, HttpStatus.BAD_REQUEST);

    }
}
