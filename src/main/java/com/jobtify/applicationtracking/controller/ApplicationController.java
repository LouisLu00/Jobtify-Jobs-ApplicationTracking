package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.service.ApplicationService;
import com.jobtify.applicationtracking.service.ValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    private final ValidationService validationService;

    // Insert by constructor
    public ApplicationController(ApplicationService applicationService, ValidationService validationService) {
        this.applicationService = applicationService;
        this.validationService = validationService;
    }

    @Operation(summary = "Get all applications by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved applications"),
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
    })
    @GetMapping("/job/{jobId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByJobId(
            @PathVariable Long jobId,
            @RequestParam(required = false) String status) {
        List<Application> applications = applicationService.getApplicationsByJobId(jobId, status);
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Get applications by application ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved application"),
    })
    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<List<Application>> getApplicationByApplicationId(@PathVariable Long applicationId) {
        List<Application> applications = applicationService.getApplicationByApplicationId(applicationId);
        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Create a new application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid application status"),
            @ApiResponse(responseCode = "404", description = "User or job not found"),
            @ApiResponse(responseCode = "408", description = "Request to user or job service timed out"),
            @ApiResponse(responseCode = "503", description = "User or job server unavailable")
    })
    @PostMapping("/{userId}/{jobId}/applications")
    public ResponseEntity<?> createApplication(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @Valid @RequestBody Application application) {
        validationService.validateUser(userId);
        validationService.validateJob(jobId);

        Application createdApplication = applicationService.createApplication(userId, jobId, application);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdApplication);
    }

    @Operation(summary = "Update an existing application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid application status"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<?> updateApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) LocalDateTime timeOfApplication) {
        Application updatedApplication = applicationService.updateApplication(applicationId, status, notes, timeOfApplication);
        return ResponseEntity.ok(updatedApplication);
    }

    @Operation(summary = "Delete an application by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Application deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Application not found")
    })
    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return ResponseEntity.noContent().build();
    }
}
