package com.jobtify.applicationtracking.repository;

import com.jobtify.applicationtracking.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
public interface JobRepository extends JpaRepository<Job, Long> {
}
