package com.jobtify.applicationtracking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Entity
@Table(name = "Job")
@Data
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    private String company;
    private String jobTitle;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String officeLocation;
    private boolean publicView;

}
