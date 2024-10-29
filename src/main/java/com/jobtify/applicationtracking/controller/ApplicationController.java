package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.service.ApplicationService;
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

    // GET: get all application by user_id
    @GetMapping("/user/{userId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByUserId(@PathVariable Long userId) {
        List<Application> applications = applicationService.getApplicationsByUserId(userId);
        return ResponseEntity.ok(applications);
    }

    // GET: get all application by job_id
    @GetMapping("/job/{jobId}/applications")
    public ResponseEntity<List<Application>> getApplicationsByJobId(@PathVariable Long jobId) {
        List<Application> applications = applicationService.getApplicationsByJobId(jobId);
        return ResponseEntity.ok(applications);
    }

    // POST: create new application
    @PostMapping("/{userId}/{jobId}/applications")
    public ResponseEntity<Application> createApplication(
            @PathVariable Long userId,
            @PathVariable Long jobId,
            @RequestBody Application application) {
        Application createdApplication = applicationService.createApplication(userId, jobId, application);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdApplication);
    }

    // PUT: update an application
    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<Application> updateApplication(
            @PathVariable Long applicationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String notes,
            @RequestParam(required = false) LocalDateTime timeOfApplication) {
        Application updatedApplication = applicationService.updateApplication(applicationId, status, notes, timeOfApplication);
        return ResponseEntity.ok(updatedApplication);
    }

    // 删除申请
    @DeleteMapping("/applications/{applicationId}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long applicationId) {
        applicationService.deleteApplication(applicationId);
        return ResponseEntity.noContent().build();
    }
}
