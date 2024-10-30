package com.jobtify.applicationtracking.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

/**
 * @author Ziyang Su
 * @version 1.0.0
 */

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        System.out.println("=======================================");
        System.out.println("Request received at " + LocalDateTime.now());
        System.out.println("HTTP Method: " + request.getMethod());
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Query Parameters: " + request.getQueryString());
        System.out.println("---------------------------------------");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        System.out.println("Response returned at " + LocalDateTime.now());
        System.out.println("Response Status: " + response.getStatus());
        System.out.println("=======================================");

        if (ex != null) {
            System.err.println("Exception occurred: " + ex.getMessage());
        }
    }
}
