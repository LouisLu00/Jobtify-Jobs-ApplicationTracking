package com.jobtify.applicationtracking.graphql;

import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.service.ApplicationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
@Controller
public class ApplicationQueryResolver {
    private final ApplicationService applicationService;

    public ApplicationQueryResolver(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @QueryMapping
    public List<Application> applicationsByUser(@Argument Long userId) {
        return applicationService.getApplicationsByUserId(userId, null);
    }
}
