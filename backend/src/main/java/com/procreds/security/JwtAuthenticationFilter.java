package com.procreds.security;

import com.procreds.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter
 * Validates JWT tokens and sets up Spring Security authentication context
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT token from request
            String jwt = getJwtFromRequest(request);

            if (jwt != null && jwtUtil.validateToken(jwt)) {
                // Extract username from token
                String username = jwtUtil.getUsernameFromToken(jwt);

                // Check if user is not already authenticated
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Load user details
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // Validate token against user details
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        // Create authentication token
                        UsernamePasswordAuthenticationToken authentication = 
                            createAuthenticationToken(jwt, userDetails, request);

                        // Set authentication in security context
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("Successfully authenticated user: {} for request: {}", 
                                username, request.getRequestURI());
                    } else {
                        log.debug("JWT token validation failed for user: {}", username);
                    }
                }
            } else if (jwt != null) {
                log.debug("Invalid JWT token for request: {}", request.getRequestURI());
            }

        } catch (Exception ex) {
            log.error("Cannot set user authentication in security context", ex);
            // Clear security context on error
            SecurityContextHolder.clearContext();
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(BEARER_PREFIX.length());
            log.debug("Extracted JWT token from Authorization header");
            return token;
        }
        
        return null;
    }

    /**
     * Create authentication token with authorities from JWT
     */
    private UsernamePasswordAuthenticationToken createAuthenticationToken(String jwt, 
                                                                         UserDetails userDetails, 
                                                                         HttpServletRequest request) {
        // Extract authorities from JWT token
        List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromToken(jwt, userDetails);

        // Create authentication token
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

        // Set authentication details
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        return authentication;
    }

    /**
     * Extract authorities from JWT token, fallback to UserDetails if not present
     */
    private List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String jwt, UserDetails userDetails) {
        try {
            // Try to extract authorities from JWT token
            String authoritiesString = jwtUtil.getAuthoritiesFromToken(jwt);
            
            if (StringUtils.hasText(authoritiesString)) {
                return Arrays.stream(authoritiesString.split(","))
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.debug("Could not extract authorities from JWT token, using UserDetails authorities", e);
        }

        // Fallback to authorities from UserDetails
        return userDetails.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());
    }

    /**
     * Skip JWT authentication for certain paths
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path, method)) {
            log.debug("Skipping JWT authentication for public endpoint: {} {}", method, path);
            return true;
        }

        // Skip authentication for static resources
        if (isStaticResource(path)) {
            log.debug("Skipping JWT authentication for static resource: {}", path);
            return true;
        }

        return false;
    }

    /**
     * Check if the request path is a public endpoint
     */
    private boolean isPublicEndpoint(String path, String method) {
        // Authentication endpoints
        if (path.startsWith("/api/auth/login") || 
            path.startsWith("/api/auth/forgot-password") || 
            path.startsWith("/api/auth/reset-password") ||
            path.startsWith("/api/auth/validate-reset-token")) {
            return true;
        }

        // Swagger/OpenAPI endpoints
        if (path.startsWith("/v3/api-docs") || 
            path.startsWith("/swagger-ui") || 
            path.equals("/swagger-ui.html") ||
            path.startsWith("/swagger-resources") || 
            path.startsWith("/webjars")) {
            return true;
        }

        // Health check endpoints
        if (path.startsWith("/actuator/health")) {
            return true;
        }

        // H2 Console (development only)
        if (path.startsWith("/h2-console")) {
            return true;
        }

        return false;
    }

    /**
     * Check if the request path is for static resources
     */
    private boolean isStaticResource(String path) {
        return path.startsWith("/css/") || 
               path.startsWith("/js/") || 
               path.startsWith("/images/") || 
               path.startsWith("/favicon.ico") ||
               path.startsWith("/static/");
    }
}
