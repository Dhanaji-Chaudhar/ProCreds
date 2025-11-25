package com.procreds.service;

import com.procreds.entity.Role;
import com.procreds.entity.User;
import com.procreds.entity.UserPlatformPermission;
import com.procreds.repository.RoleRepository;
import com.procreds.repository.UserPlatformPermissionRepository;
import com.procreds.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserPlatformPermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting data initialization...");
        
        initializeRoles();
        initializeAdminUser();
        
        log.info("Data initialization completed successfully");
    }

    private void initializeRoles() {
        log.info("Initializing roles...");
        
        // Create ADMIN role
        if (!roleRepository.existsByName(Role.ADMIN)) {
            Role adminRole = new Role();
            adminRole.setName(Role.ADMIN);
            adminRole.setDescription("System administrator with full access to all features and platforms");
            roleRepository.save(adminRole);
            log.info("Created ADMIN role");
        }
        
        // Create PLATFORM_ADMIN role
        if (!roleRepository.existsByName(Role.PLATFORM_ADMIN)) {
            Role platformAdminRole = new Role();
            platformAdminRole.setName(Role.PLATFORM_ADMIN);
            platformAdminRole.setDescription("Platform administrator with access to specific platforms");
            roleRepository.save(platformAdminRole);
            log.info("Created PLATFORM_ADMIN role");
        }
        
        // Create USER role
        if (!roleRepository.existsByName(Role.USER)) {
            Role userRole = new Role();
            userRole.setName(Role.USER);
            userRole.setDescription("Standard user with platform-specific permissions");
            roleRepository.save(userRole);
            log.info("Created USER role");
        }
        
        log.info("Roles initialization completed");
    }

    private void initializeAdminUser() {
        log.info("Initializing admin user...");
        
        String adminUsername = "admin";
        String adminEmail = "admin@procreds.com";
        String adminPassword = "admin123"; // Should be changed on first login
        
        if (!userRepository.existsByUsername(adminUsername)) {
            // Get ADMIN role
            Role adminRole = roleRepository.findByName(Role.ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            
            // Create admin user
            User adminUser = new User();
            adminUser.setUsername(adminUsername);
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setFirstName("System");
            adminUser.setLastName("Administrator");
            adminUser.setEnabled(true);
            adminUser.setAccountNonExpired(true);
            adminUser.setCredentialsNonExpired(true);
            adminUser.setFailedLoginAttempts(0);
            
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);
            
            adminUser = userRepository.save(adminUser);
            
            // Grant admin access to all platforms
            createAdminPlatformPermissions(adminUser);
            
            log.info("Created admin user: {} with email: {}", adminUsername, adminEmail);
            log.warn("IMPORTANT: Default admin password is '{}' - CHANGE THIS IMMEDIATELY!", adminPassword);
        } else {
            log.info("Admin user already exists, skipping creation");
        }
        
        log.info("Admin user initialization completed");
    }

    private void createAdminPlatformPermissions(User adminUser) {
        log.info("Creating admin platform permissions...");
        
        List<String> platforms = List.of(
            UserPlatformPermission.GITHUB,
            UserPlatformPermission.BITBUCKET,
            UserPlatformPermission.GITLAB,
            UserPlatformPermission.JENKINS,
            UserPlatformPermission.JIRA,
            UserPlatformPermission.SONARQUBE,
            UserPlatformPermission.KUBERNETES,
            UserPlatformPermission.AWS_EKS,
            UserPlatformPermission.AZURE_AKS,
            UserPlatformPermission.GCP_GKE
        );
        
        for (String platform : platforms) {
            if (!permissionRepository.existsByUserAndPlatform(adminUser, platform)) {
                UserPlatformPermission permission = new UserPlatformPermission();
                permission.setUser(adminUser);
                permission.setPlatform(platform);
                permission.setPermissionLevel(UserPlatformPermission.PermissionLevel.ADMIN);
                permission.setEnabled(true);
                permission.setGrantedBy(adminUser); // Self-granted for initial setup
                
                permissionRepository.save(permission);
                log.debug("Granted ADMIN permission to platform: {}", platform);
            }
        }
        
        log.info("Admin platform permissions created for {} platforms", platforms.size());
    }
}

