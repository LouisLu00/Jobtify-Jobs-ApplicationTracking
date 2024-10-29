package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@RestController
@RequestMapping("/api/user")
public class ApplicationController {
    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/{userId}/applications")
    public List<Application> getUserApplications(@PathVariable Long userId) {
        return applicationService.getApplicationsByUserId(userId);
    }

    @PostMapping("/{userId}/applications")
    public ResponseEntity<Application> createApplication(@PathVariable Long userId, @RequestBody Application application) {
        Application createApplication = applicationService.createApplication(userId, application);
        return ResponseEntity.status(HttpStatus.CREATED).body(createApplication);
    }

    @PutMapping("/applications/{applicationId}")
    public ResponseEntity<Application> updateApplication(@PathVariable Long applicationId, @RequestParam String status) {
        Application updatedApplication = applicationService.updateApplicationStatus(applicationId, status);
        return ResponseEntity.ok(updatedApplication);
    }
}
