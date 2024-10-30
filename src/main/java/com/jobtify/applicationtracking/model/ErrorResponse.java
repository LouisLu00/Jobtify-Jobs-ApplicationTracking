package com.jobtify.applicationtracking.model;

import lombok.Data;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Data
public class ErrorResponse {
    private int statusCode;
    private String message;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
