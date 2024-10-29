package com.jobtify.applicationtracking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Entity
@Table(name = "application")
@Data
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    private Long userId;
    private Long jobId;

    private LocalDateTime timeOfApplication;

    @Column(nullable = false)
    private String applicationStatus;

    private String notes;
}
