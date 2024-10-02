package com.jobtify.applicationtracking.repository;

import com.jobtify.applicationtracking.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    @Query("SELECT a FROM Application a JOIN UserApplied ua ON a.applicationId = ua.id.applicationId WHERE ua.id.userId = :userId")
    List<Application> findByUserId(Long userId);

    @Query("SELECT r.job.jobId FROM ReceivedApplication r WHERE r.application.applicationId = :applicationId")
    Long findJobIdByApplicationId(Long applicationId);

}
