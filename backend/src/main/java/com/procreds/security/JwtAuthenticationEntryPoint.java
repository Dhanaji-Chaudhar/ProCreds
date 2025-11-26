package com.procreds.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point
 * Handles authentication errors and sends appropriate HTTP responses
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        log.error("Unauthorized error: {}", authException.getMessage());
        log.debug("Request URI: {}", request.getRequestURI());
        log.debug("Request Method: {}", request.getMethod());
        
        // Set response status and content type
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        // Create error response body
        Map<String, Object> errorResponse = createErrorResponse(request, authException);
        
        // Write error response to output stream
        response.getOutputStream().println(objectMapper.writeValueAsString(errorResponse));
    }

    /**
     * Create standardized error response
     */
    private Map<String, Object> createErrorResponse(HttpServletRequest request, 
                                                   AuthenticationException authException) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", determineErrorMessage(authException));
        errorResponse.put("path", request.getRequestURI());
        
        // Add additional context for debugging (only in development)
        if (log.isDebugEnabled()) {
            errorResponse.put("exception", authException.getClass().getSimpleName());
            errorResponse.put("method", request.getMethod());
            
            // Add request headers for debugging
            Map<String, String> headers = new HashMap<>();
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> 
                headers.put(headerName, request.getHeader(headerName))
            );
            errorResponse.put("headers", headers);
        }
        
        return errorResponse;
    }

    /**
     * Determine appropriate error message based on exception type
     */
    private String determineErrorMessage(AuthenticationException authException) {
        String message = authException.getMessage();
        
        // Provide user-friendly messages for common scenarios
        if (message == null || message.trim().isEmpty()) {
            return "Authentication required";
        }
        
        // Handle specific authentication error types
        if (message.contains("JWT")) {
            return "Invalid or expired authentication token";
        }
        
        if (message.contains("Access Denied")) {
            return "Access denied - insufficient privileges";
        }
        
        if (message.contains("Bad credentials")) {
            return "Invalid username or password";
        }
        
        if (message.contains("Account is disabled")) {
            return "Account is disabled";
        }
        
        if (message.contains("Account is locked")) {
            return "Account is locked";
        }
        
        // Default message for security
        return "Authentication required";
    }
}
