package com.jobtify.applicationtracking.event;

import com.jobtify.applicationtracking.model.Application;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
@Getter
public class ApplicationCreatedEvent extends ApplicationEvent {
    private final Application application;

    public ApplicationCreatedEvent(Object source, Application application) {
        super(source);
        this.application = application;
    }

}
