package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.model.ErrorResponse;
import com.jobtify.applicationtracking.service.ApplicationService;
import com.jobtify.applicationtracking.util.ErrorResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@RestController
@RequestMapping("/api/application")
public class ApplicationController {
    private final ApplicationService applicationService;

    // Insert by constructor
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Operation(summary = "Get all applications by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved applications"),
            @ApiResponse(responseCode = "404", description = "Error")
    })
    @GetMapping("/user/{userId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        List<Application> applications = applicationService.getApplicationsByUserId(userId, status);
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Get all applications by job ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved applications"),
            @ApiResponse(responseCode = "404", description = "Error")
    })
    @GetMapping("/job/{jobId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByJobId(
            @PathVariable Long jobId,
            @RequestParam(required = false) String status) {
        List<Application> applications = applicationService.getApplicationsByJobId(jobId, status);
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Create a new application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User or job not found")
    })
    @PostMapping("/{userId}/{jobId}/applications")
    public ResponseEntity<?> createApplication(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @RequestBody Application application) {

        try {
            Application createdApplication = applicationService.createApplication(userId, jobId, application);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdApplication);
        } catch (IllegalArgumentException e) {
            return ErrorResponseUtil.generateBadRequestResponse(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ErrorResponseUtil.generateNotFoundResponse(e.getMessage());
            }
            return ErrorResponseUtil.generateServerErrorResponse("Server error: " + e.getMessage());
        }
    }

    @Operation(summary = "Update an existing application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application updated successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<?> updateApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) LocalDateTime timeOfApplication) {
        try {
            Application updatedApplication = applicationService.updateApplication(applicationId, status, notes, timeOfApplication);
            return ResponseEntity.ok(updatedApplication);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage().contains("not found") ?
                    "Application with ID " + applicationId + " not found" : "Server error: " + e.getMessage();
            HttpStatus httpStatus = errorMessage.contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(httpStatus).body(new ErrorResponse(httpStatus.value(), errorMessage));
        }
    }

    @Operation(summary = "Delete an application by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Application deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long applicationId) {
        if (applicationService.deleteApplication(applicationId)) {
            return ResponseEntity.noContent().build();
        } else {
            return ErrorResponseUtil.generateNotFoundResponse("Application with ID " + applicationId + " not found");
        }
    }
}
