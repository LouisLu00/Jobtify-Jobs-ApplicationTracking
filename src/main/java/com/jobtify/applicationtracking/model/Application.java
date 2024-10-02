package com.jobtify.applicationtracking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Entity
@Table(name = "Application")
@Data
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long applicationId;

    private LocalDateTime timeOfApplication;
    private String applicationStatus;

}
