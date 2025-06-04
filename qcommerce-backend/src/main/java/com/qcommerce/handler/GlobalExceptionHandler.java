package com.qcommerce.handler;

// Import generated Model from the package structure (without Dto suffix)
import com.qcommerce.generated.model.Error;

// Import custom exceptions from the package structure
import com.qcommerce.exception.InvalidInputException;
import com.qcommerce.exception.UserAlreadyExistsException;

import org.openapitools.jackson.nullable.JsonNullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        String errorMessage = "Validation failed: " + ex.getBindingResult().getAllErrors().stream()
                                .map(error -> {
                                    if (error instanceof FieldError) {
                                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                                    }
                                    return error.getDefaultMessage();
                                })
                                .collect(Collectors.joining(", "));
        
        Error errorModel = new Error();
        errorModel.setMessage(errorMessage);
        errorModel.setCode(JsonNullable.of("INVALID_INPUT"));

        logger.warn("Validation error: {} - Path: {}", errorMessage, request.getDescription(false), ex);
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Error> handleUserAlreadyExistsException(
            UserAlreadyExistsException ex, WebRequest request) {
        Error errorModel = new Error();
        errorModel.setMessage(ex.getMessage());
        errorModel.setCode(JsonNullable.of("USER_ALREADY_EXISTS")); 

        logger.warn("User already exists: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Error> handleInvalidInputException(
            InvalidInputException ex, WebRequest request) {
        Error errorModel = new Error();
        errorModel.setMessage(ex.getMessage());
        errorModel.setCode(JsonNullable.of("INVALID_INPUT_DATA"));
        // Assuming your 'Error' model (generated from schema) has 'message', 'code', and optionally 'details'
        // If ex.getDetails() is not null and your 'Error' model has a 'setDetails' method:
        // if (ex.getDetails() != null) {
        //    try {
        //        // Check if the Error model has a setDetails method.
        //        Method setDetailsMethod = errorModel.getClass().getMethod("setDetails", Object.class);
        //        setDetailsMethod.invoke(errorModel, ex.getDetails());
        //    } catch (NoSuchMethodException e) {
        //        logger.trace("Error model does not have a setDetails(Object) method.");
        //    } catch (Exception e) {
        //        logger.warn("Could not set details on error model", e);
        //    }
        // }
        logger.warn("Invalid input data: {} - Details: {} - Path: {}", ex.getMessage(), ex.getDetails(), request.getDescription(false));
        return new ResponseEntity<>(errorModel, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Error> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        Error errorModel = new Error();
        errorModel.setMessage(ex.getReason() != null ? ex.getReason() : "An error occurred.");
        errorModel.setCode(JsonNullable.of(ex.getStatusCode().toString()));

        logger.warn("ResponseStatusException: {} - Path: {}", ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorModel, ex.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleGlobalException(
            Exception ex, WebRequest request) {
        logger.error("An unexpected error occurred: Path: {}", request.getDescription(false), ex); 
        
        Error errorModel = new Error();
        errorModel.setMessage("An internal server error occurred. Please try again later.");
        errorModel.setCode(JsonNullable.of("INTERNAL_SERVER_ERROR"));

        return new ResponseEntity<>(errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
