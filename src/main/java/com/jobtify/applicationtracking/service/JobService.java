package com.jobtify.applicationtracking.service;

import com.jobtify.applicationtracking.model.Job;
import com.jobtify.applicationtracking.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;

    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
    }
}
