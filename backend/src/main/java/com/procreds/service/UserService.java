package com.procreds.service;

import com.procreds.dto.UserProfileResponse;
import com.procreds.dto.UserRegistrationRequest;
import com.procreds.entity.Role;
import com.procreds.entity.User;
import com.procreds.entity.UserPlatformPermission;
import com.procreds.exception.ResourceNotFoundException;
import com.procreds.exception.BadRequestException;
import com.procreds.repository.RoleRepository;
import com.procreds.repository.UserPlatformPermissionRepository;
import com.procreds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPlatformPermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public User createUser(UserRegistrationRequest request, User createdBy) {
        log.info("Creating new user: {}", request.getUsername());
        
        // Validate unique constraints
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        
        // Create user entity
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(request.isEnabled());
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setFailedLoginAttempts(0);
        
        // Assign roles
        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
                roles.add(role);
            }
        } else {
            // Default to USER role
            Role userRole = roleRepository.findByName(Role.USER)
                .orElseThrow(() -> new BadRequestException("Default USER role not found"));
            roles.add(userRole);
        }
        user.setRoles(roles);
        
        // Save user
        user = userRepository.save(user);
        
        // Create platform permissions
        if (request.getPlatformPermissions() != null) {
            for (UserRegistrationRequest.PlatformPermissionRequest permRequest : request.getPlatformPermissions()) {
                UserPlatformPermission permission = new UserPlatformPermission();
                permission.setUser(user);
                permission.setPlatform(permRequest.getPlatform());
                permission.setPermissionLevel(UserPlatformPermission.PermissionLevel.valueOf(permRequest.getPermissionLevel()));
                permission.setEnabled(permRequest.isEnabled());
                permission.setGrantedBy(createdBy);
                
                permissionRepository.save(permission);
            }
        }
        
        // Log audit event
        auditService.logUserCreate(createdBy, user);
        
        log.info("Successfully created user: {} with ID: {}", user.getUsername(), user.getId());
        return user;
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> findUsersByEnabled(boolean enabled, Pageable pageable) {
        return userRepository.findByEnabled(enabled, pageable);
    }

    @Transactional(readOnly = true)
    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findBySearchTerm(searchTerm, pageable);
    }

    public User updateUser(Long id, UserRegistrationRequest request, User updatedBy) {
        log.info("Updating user with ID: {}", id);
        
        User user = findById(id);
        
        // Check for unique constraints (excluding current user)
        if (!user.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already exists: " + request.getUsername());
        }
        
        if (!user.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }
        
        // Update user fields
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(request.isEnabled());
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        // Update roles if provided
        if (request.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }
        
        user = userRepository.save(user);
        
        // Log audit event
        auditService.logUserUpdate(updatedBy, user);
        
        log.info("Successfully updated user: {}", user.getUsername());
        return user;
    }

    public void enableUser(Long id, User enabledBy) {
        log.info("Enabling user with ID: {}", id);
        
        User user = findById(id);
        user.setEnabled(true);
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        
        userRepository.save(user);
        auditService.logUserEnable(enabledBy, user);
        
        log.info("Successfully enabled user: {}", user.getUsername());
    }

    public void disableUser(Long id, User disabledBy) {
        log.info("Disabling user with ID: {}", id);
        
        User user = findById(id);
        user.setEnabled(false);
        
        userRepository.save(user);
        auditService.logUserDisable(disabledBy, user);
        
        log.info("Successfully disabled user: {}", user.getUsername());
    }

    public void deleteUser(Long id, User deletedBy) {
        log.info("Deleting user with ID: {}", id);
        
        User user = findById(id);
        
        // Delete all platform permissions first
        permissionRepository.deleteByUser(user);
        
        // Delete user
        userRepository.delete(user);
        
        // Log audit event
        auditService.logUserDelete(deletedBy, user);
        
        log.info("Successfully deleted user: {}", user.getUsername());
    }

    public void updateLastLogin(String username) {
        User user = findByUsername(username);
        user.setLastLogin(LocalDateTime.now());
        user.setFailedLoginAttempts(0); // Reset failed attempts on successful login
        userRepository.save(user);
    }

    public void incrementFailedLoginAttempts(String username) {
        User user = findByUsername(username);
        user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
        
        // Lock account after 5 failed attempts for 30 minutes
        if (user.getFailedLoginAttempts() >= 5) {
            user.setAccountLockedUntil(LocalDateTime.now().plusMinutes(30));
            log.warn("Account locked due to failed login attempts: {}", username);
        }
        
        userRepository.save(user);
    }

    public void unlockUser(Long id, User unlockedBy) {
        log.info("Unlocking user with ID: {}", id);
        
        User user = findById(id);
        user.setAccountLockedUntil(null);
        user.setFailedLoginAttempts(0);
        
        userRepository.save(user);
        auditService.logUserUpdate(unlockedBy, user);
        
        log.info("Successfully unlocked user: {}", user.getUsername());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = findById(userId);
        return convertToUserProfileResponse(user);
    }

    @Transactional(readOnly = true)
    public List<String> getUserAccessiblePlatforms(Long userId) {
        return permissionRepository.findEnabledPlatformsByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean hasUserPlatformAccess(Long userId, String platform) {
        return permissionRepository.existsByUserIdAndPlatformAndEnabled(userId, platform, true);
    }

    @Transactional(readOnly = true)
    public UserPlatformPermission.PermissionLevel getUserPlatformPermissionLevel(Long userId, String platform) {
        return permissionRepository.findByUserIdAndPlatformAndEnabled(userId, platform, true)
            .map(UserPlatformPermission::getPermissionLevel)
            .orElse(null);
    }

    private UserProfileResponse convertToUserProfileResponse(User user) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setFullName(user.getFullName());
        response.setEnabled(user.getEnabled());
        response.setLastLogin(user.getLastLogin());
        response.setCreatedAt(user.getCreatedAt());
        
        // Convert roles
        List<String> roles = user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList());
        response.setRoles(roles);
        
        // Convert platform permissions
        List<UserProfileResponse.PlatformPermissionResponse> permissions = 
            user.getPlatformPermissions().stream()
                .map(this::convertToPlatformPermissionResponse)
                .collect(Collectors.toList());
        response.setPlatformPermissions(permissions);
        
        return response;
    }

    private UserProfileResponse.PlatformPermissionResponse convertToPlatformPermissionResponse(UserPlatformPermission permission) {
        UserProfileResponse.PlatformPermissionResponse response = new UserProfileResponse.PlatformPermissionResponse();
        response.setPlatform(permission.getPlatform());
        response.setPermissionLevel(permission.getPermissionLevel().name());
        response.setEnabled(permission.getEnabled());
        response.setGrantedAt(permission.getCreatedAt());
        response.setGrantedBy(permission.getGrantedBy() != null ? permission.getGrantedBy().getUsername() : null);
        return response;
    }
}

