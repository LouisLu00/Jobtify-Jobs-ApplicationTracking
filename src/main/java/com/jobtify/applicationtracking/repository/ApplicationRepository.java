package com.jobtify.applicationtracking.repository;

import com.jobtify.applicationtracking.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long userId);

    List<Application> findByJobId(Long jobId);
}
