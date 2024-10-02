package com.jobtify.applicationtracking.repository;

import com.jobtify.applicationtracking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmailAndPassword(String email, String password);
}
