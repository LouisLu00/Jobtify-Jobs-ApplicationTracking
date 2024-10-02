package com.jobtify.applicationtracking.controller;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
