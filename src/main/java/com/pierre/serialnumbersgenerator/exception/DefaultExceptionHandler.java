package com.pierre.serialnumbersgenerator.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler {

    private static final String MAIN_MESSAGE = """
            Server Error: %s
            On URL:       %s
            """;

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> defaultExceptionHandler(HttpServletRequest req, Exception e) throws Exception {

        log.error(e.getMessage());

        if (AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class) != null)
            throw e;

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(String.format(MAIN_MESSAGE, e.getMessage(), req.getRequestURL()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidSetNameException.class)
    public ResponseEntity<String> invalidSetNameExceptionHandler(Exception e) {

        log.error(e.getMessage());
        return ResponseEntity
                .unprocessableEntity()
                .body(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidSetQuantityException.class)
    public ResponseEntity<String> invalidSetQuantityExceptionHandler(Exception e) {

        log.error(e.getMessage());
        return ResponseEntity
                .unprocessableEntity()
                .body(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(SetNotFoundException.class)
    public ResponseEntity<String> setNotFoundExceptionHandler(Exception e) {

        log.error(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler(SetNotReadyException.class)
    public ResponseEntity<String> setNotReadyExceptionHandler(Exception e) {

        log.error(e.getMessage());
        return ResponseEntity
                .accepted()
                .body(e.getMessage());
    }
}