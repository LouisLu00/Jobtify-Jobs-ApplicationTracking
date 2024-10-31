package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.model.ErrorResponse;
import com.jobtify.applicationtracking.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/user/{userId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        List<Application> applications = applicationService.getApplicationsByUserId(userId, status);

        List<EntityModel<Application>> applicationModels = applications.stream()
                .map(application -> EntityModel.of(application,
                        linkTo(methodOn(ApplicationController.class).getApplicationsByUserId(userId, null)).withRel("userApplications"),
                        linkTo(methodOn(ApplicationController.class).getApplicationsByJobId(application.getJobId(), null)).withRel("jobApplications")
                ))
                .toList();

        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Get all applications by job ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved applications"),
            @ApiResponse(responseCode = "404", description = "Job not found")
    })
    @GetMapping("/job/{jobId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByJobId(
            @PathVariable Long jobId,
            @RequestParam(required = false) String status) {
        List<Application> applications = applicationService.getApplicationsByJobId(jobId, status);

        List<EntityModel<Application>> applicationModels = applications.stream()
                .map(application -> EntityModel.of(application,
                        linkTo(methodOn(ApplicationController.class).getApplicationsByUserId(application.getUserId(), null)).withRel("userApplications"),
                        linkTo(methodOn(ApplicationController.class).getApplicationsByJobId(jobId, null)).withRel("jobApplications")
                ))
                .toList();

        return ResponseEntity.ok(applications);
    }

    @Operation(summary = "Create a new application")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Application created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/{userId}/{jobId}/applications")
    public ResponseEntity<?> createApplication(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @RequestBody Application application) {

        try {
            if (!applicationService.validateUser(userId)) {
                String errorMessage = "User with ID " + userId + " not found.";
                System.err.println(errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage));
            }
        } catch (RuntimeException e) {
            String errorMessage = "Error connecting to user service: " + e.getMessage();
            System.err.println(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage));
        }

        try {
            if (!applicationService.validateJob(jobId)) {
                String errorMessage = "Job with ID " + jobId + " not found.";
                System.err.println(errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage));
            }
        } catch (RuntimeException e) {
            String errorMessage = "Error connecting to job service: " + e.getMessage();
            System.err.println(errorMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage));
        }

        Application createdApplication = applicationService.createApplication(userId, jobId, application);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdApplication);
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
        try {
            applicationService.deleteApplication(applicationId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage().contains("not found") ?
                    "Application with ID " + applicationId + " not found" : "Server error: " + e.getMessage();
            HttpStatus status = errorMessage.contains("not found") ? HttpStatus.NOT_FOUND : HttpStatus.INTERNAL_SERVER_ERROR;
            return ResponseEntity.status(status).body(new ErrorResponse(status.value(), errorMessage));
        }
    }
}
