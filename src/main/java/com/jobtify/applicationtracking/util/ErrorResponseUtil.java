package com.jobtify.applicationtracking.util;

import com.jobtify.applicationtracking.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
public class ErrorResponseUtil {

    public static ResponseEntity<ErrorResponse> generateBadRequestResponse(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
    }

    public static ResponseEntity<ErrorResponse> generateNotFoundResponse(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), message));
    }

    public static ResponseEntity<ErrorResponse> generateServerErrorResponse(String message) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message));
    }
}
