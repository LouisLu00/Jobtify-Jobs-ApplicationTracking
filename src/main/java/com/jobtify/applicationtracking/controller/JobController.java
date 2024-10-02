package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Job;
import com.jobtify.applicationtracking.service.ApplicationService;
import com.jobtify.applicationtracking.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@RestController
@RequestMapping("api/application")
public class JobController {
    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/{applicationId}/job")
    public ResponseEntity<?> getJobForApplication(@PathVariable Long applicationId) {
        Long jobId = applicationService.getJobIdByApplicationId(applicationId);
        if (jobId != null) {
            Job job = jobService.getJobById(jobId);
            return ResponseEntity.ok(job);
        } else {
            return ResponseEntity.status(404).body("Job not found for the application");
        }
    }
}
