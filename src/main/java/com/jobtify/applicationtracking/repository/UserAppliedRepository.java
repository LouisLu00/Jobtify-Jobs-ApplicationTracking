package com.jobtify.applicationtracking.repository;

import com.jobtify.applicationtracking.model.UserApplied;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
public interface UserAppliedRepository extends JpaRepository<UserApplied, UserApplied.UserAppliedKey> {

}
