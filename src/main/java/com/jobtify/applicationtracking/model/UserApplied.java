package com.jobtify.applicationtracking.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Entity
@Table(name = "User_Applied")
@Data
public class UserApplied {
    @EmbeddedId
    private UserAppliedKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("applicationId")
    @JoinColumn(name = "application_id")
    private Application application;

}

@Embeddable
@Data
class UserAppliedKey implements Serializable {
    private Long userId;
    private Long applicationId;

}
