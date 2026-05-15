package com.springProject.Practice.exception;

import com.springProject.Practice.dto.ResponseAPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    GlobalExceptionHandler.class
            );

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseAPI>handleAuthenticationException(FileNotFoundException ex){
        log.warn("Authentication  error : {}",ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseAPI(ex.getMessage(),false,null));
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseAPI>handleUserNotFoundException(UserNotFoundException ex){
        log.warn("User not found: {}",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseAPI(ex.getMessage(),false,null));
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseAPI>handleFileNotFoundException(FileNotFoundException ex){
        log.warn("File not found: {}",ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseAPI(ex.getMessage(),false,null));
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ResponseAPI>handleStorageException(FileStorageException ex) {

        log.error("Storage error: {}",ex.getMessage(),ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseAPI(ex.getMessage(),false,null));
    }

    @ExceptionHandler(
            AccessDeniedException.class
    )
    public ResponseEntity<ResponseAPI>
    handleAccessDenied(
            AccessDeniedException ex
    ) {

        log.warn(
                "Access denied: {}",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(
                        new ResponseAPI(
                                ex.getMessage(),
                                false,
                                null
                        )
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, String>>
    handleValidation(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors =
                new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> {

                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    );
                });

        log.warn(
                "Validation failed: {}",
                errors
        );

        return ResponseEntity
                .badRequest()
                .body(errors);
    }

//    @ExceptionHandler(
//            FileNotFoundException.class
//    )
//    public ResponseEntity<ResponseAPI>
//    handleFileNotFound(
//            FileNotFoundException ex
//    ) {
//
//        log.warn(
//                "File not found: {}",
//                ex.getMessage()
//        );
//
//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND)
//                .body(
//                        new ResponseAPI(
//                                ex.getMessage(),
//                                false,
//                                null
//                        )
//                );
//    }

    @ExceptionHandler(
            UserAlreadyExistsException.class
    )
    public ResponseEntity<ResponseAPI>
    handleUserAlreadyExists(
            UserAlreadyExistsException ex
    ) {

        log.warn(
                "User already exists: {}",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        new ResponseAPI(
                                ex.getMessage(),
                                false,
                                null
                        )
                );
    }

    @ExceptionHandler(
            PasswordMismatchException.class
    )
    public ResponseEntity<ResponseAPI>
    handlePasswordMismatch(
            PasswordMismatchException ex
    ) {

        log.warn(
                "Authentication failed: {}",
                ex.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ResponseAPI(
                                ex.getMessage(),
                                false,
                                null
                        )
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseAPI>
    handleGenericException(
            Exception ex
    ) {

        log.error(
                "Unexpected error occurred",
                ex
        );

        return ResponseEntity
                .status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        new ResponseAPI(
                                "Something went wrong",
                                false,
                                null
                        )
                );
    }
}