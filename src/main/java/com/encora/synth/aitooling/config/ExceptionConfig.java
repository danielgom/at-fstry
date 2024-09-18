package com.encora.synth.aitooling.config;

import com.encora.synth.aitooling.dto.Error;
import com.encora.synth.aitooling.dto.exception.UserException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionConfig extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {UserException.class})
    protected ResponseEntity<Error> handleSmileUserException(UserException ex, WebRequest request) {
        Error error = Error.builder()
                .reason(ex.getLocalizedMessage())
                .timestamp(LocalDateTime.now())
                .error(ex.getStatus().name())
                .status(ex.getStatus().value())
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .build();

        return new ResponseEntity<>(error, ex.getStatus());
    }
}
