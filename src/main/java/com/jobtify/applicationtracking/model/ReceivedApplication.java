package com.jobtify.applicationtracking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
@Entity
@Table(name = "Received_Application")
@Data
public class ReceivedApplication {

    @EmbeddedId
    private ReceivedApplicationKey id;

    @ManyToOne
    @MapsId("jobId")
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @MapsId("applicationId")
    @JoinColumn(name = "application_id")
    private Application application;

}

@Embeddable
@Data
class ReceivedApplicationKey implements Serializable {
    private Long jobId;
    private Long applicationId;

}
