package com.jobtify.applicationtracking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtify.applicationtracking.controller.ApplicationController;
import com.jobtify.applicationtracking.model.Application;
import com.jobtify.applicationtracking.service.ApplicationService;
import com.jobtify.applicationtracking.service.ValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */
@WebMvcTest(ApplicationController.class)
public class ApplicationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplicationService applicationService;

    @MockBean
    private ValidationService validationService;

    private Long createdApplicationId;

    @BeforeEach
    void setUp() {
        Application testApplication = new Application();
        testApplication.setUserId(11L);
        testApplication.setJobId(10L);
        testApplication.setApplicationStatus("applied");
    }

    @Test
    void testGetApplicationsByUserId() throws Exception {
        Application application = new Application();
        application.setUserId(11L);

        Mockito.when(applicationService.getApplicationsByUserId(11L, null))
                .thenReturn(Collections.singletonList(application));

        mockMvc.perform(get("/api/application/user/11/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(11)));
    }

    @Test
    void testGetApplicationsByJobId() throws Exception {
        Application application = new Application();
        application.setJobId(11L);

        Mockito.when(applicationService.getApplicationsByJobId(11L, null))
                .thenReturn(Collections.singletonList(application));

        mockMvc.perform(get("/api/application/job/11/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].jobId", is(11)));
    }

}
