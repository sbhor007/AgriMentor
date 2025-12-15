package com.server.controller;

import com.server.dto.adminDTO.*;
import com.server.entity.Consultation;
import com.server.entity.User;
import com.server.enumeration.Role;
import com.server.response.ApiResponse;
import com.server.service.AdminService;
import com.server.service.ConsultantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.server.dto.consultantDTO.ConsultantResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin")
@Slf4j
public class AdminController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private ConsultantService consultantService;

    // ==================== Dashboard Statistics Endpoints ====================

    /**
     * Get comprehensive dashboard statistics
     */
    @GetMapping("/dashboard/statistics")
    public ResponseEntity<?> getDashboardStatistics(Authentication authentication) {
        log.info("Fetching dashboard statistics");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to dashboard statistics");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing dashboard statistics", authentication.getName());
        try {
            AdminDashboardStatsDTO stats = adminService.getDashboardStatistics();
            log.info("Dashboard statistics fetched successfully");
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Dashboard statistics retrieved successfully", stats));
        } catch (Exception e) {
            log.error("Error fetching dashboard statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    /**
     * Get detailed user statistics
     */
    @GetMapping("/statistics/users")
    public ResponseEntity<?> getUserStatistics(Authentication authentication) {
        log.info("Fetching user statistics");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to user statistics");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing user statistics", authentication.getName());
        try {
            UserStatisticsDTO stats = adminService.getUserStatistics();
            log.info("User statistics fetched successfully");
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "User statistics retrieved successfully", stats));
        } catch (Exception e) {
            log.error("Error fetching user statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    /**
     * Get consultation overview statistics
     */
    @GetMapping("/statistics/consultations")
    public ResponseEntity<?> getConsultationStatistics(Authentication authentication) {
        log.info("Fetching consultation statistics");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to consultation statistics");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing consultation statistics", authentication.getName());
        try {
            ConsultationOverviewDTO overview = adminService.getConsultationOverview();
            log.info("Consultation statistics fetched successfully");
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Consultation statistics retrieved successfully", overview));
        } catch (Exception e) {
            log.error("Error fetching consultation statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    // ==================== User Management Endpoints ====================

    /**
     * Get all users with optional role filtering
     */
    @GetMapping("/users/all")
    public ResponseEntity<?> getAllUsers(@RequestParam(required = false) String role, Authentication authentication) {
        log.info("Fetching all users with role filter: {}", role);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get all users");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing all users", authentication.getName());
        try {
            Role roleEnum = null;
            if (role != null && !role.isEmpty()) {
                roleEnum = Role.valueOf(role.toUpperCase());
            }

            List<User> users = adminService.getAllUsers(roleEnum);
            log.info("Users fetched successfully. Total: {}", users.size());
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Users retrieved successfully", users));
        } catch (IllegalArgumentException e) {
            log.error("Invalid role parameter: {}", role);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Invalid role parameter: " + role));
        } catch (Exception e) {
            log.error("Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    /**
     * Get all farmers
     */
    @GetMapping("/farmers/all")
    public ResponseEntity<?> getAllFarmers(Authentication authentication) {
        log.info("Fetching all farmers");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get all farmers");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing all farmers", authentication.getName());
        try {
            List<User> farmers = adminService.getAllFarmers();
            log.info("Farmers fetched successfully. Total: {}", farmers.size());
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Farmers retrieved successfully", farmers));
        } catch (Exception e) {
            log.error("Error fetching farmers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    /**
     * Toggle user active status
     */
    @PutMapping("/users/{username}/toggle-status")
    public ResponseEntity<?> toggleUserStatus(@PathVariable String username, Authentication authentication) {
        log.info("Toggling user status for: {}", username);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to toggle user status");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} toggling status for user: {}", authentication.getName(), username);
        try {
            boolean success = adminService.toggleUserStatus(username);
            if (success) {
                log.info("User status toggled successfully for: {}", username);
                return ResponseEntity.ok().body(
                        new ApiResponse<>(HttpStatus.OK, "User status toggled successfully"));
            } else {
                log.error("Failed to toggle user status for: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, "User not found"));
            }
        } catch (Exception e) {
            log.error("Error toggling user status for: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    // ==================== Consultant Management Endpoints ====================

    @GetMapping("/all/consultants")
    public ResponseEntity<?> getAllConsultants(Authentication authentication) {
        log.info("Fetching all consultants");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get all consultants");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing all consultants", authentication.getName());
        try {
            List<ConsultantResponse> consultants = consultantService.getAllConsultants();
            log.info("Consultants fetched successfully. Total: {}", consultants.size());
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Consultants retrieved successfully", consultants));
        } catch (Exception e) {
            log.error("Error fetching consultants", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, e.getLocalizedMessage()));

        }
    }

    @PutMapping("/verify/consultant/{username}")
    public ResponseEntity<?> verifyConsultant(@PathVariable String username, Authentication authentication) {
        log.info("Verifying consultant with username: {}", username);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to verify consultant");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} verifying consultant: {}", authentication.getName(), username);
        try {
            if (!consultantService.verifyConsultant(username)) {
                log.error("Consultant verification failed for username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Consultant verification failed"));
            }
            log.info("Consultant verified successfully");
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Consultant verified successfully"));
        } catch (Exception e) {
            log.error("Error verifying consultant", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()));
        }
    }

    @PutMapping("/reject/consultant/{username}")
    public ResponseEntity<?> rejectConsultant(@PathVariable String username, Authentication authentication) {
        log.info("Rejecting consultant with username: {}", username);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to reject consultant");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} rejecting consultant: {}", authentication.getName(), username);
        try {
            if (!adminService.rejectConsultant(username)) {
                log.error("Consultant rejection failed for username: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Consultant rejection failed"));
            }
            log.info("Consultant rejected successfully");
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Consultant rejected successfully"));
        } catch (Exception e) {
            log.error("Error rejecting consultant", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, e.getLocalizedMessage()));
        }
    }

    // ==================== Consultation Management Endpoints ====================

    /**
     * Get all consultations
     */
    @GetMapping("/consultations/all")
    public ResponseEntity<?> getAllConsultations(Authentication authentication) {
        log.info("Fetching all consultations");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get all consultations");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing all consultations", authentication.getName());
        try {
            List<Consultation> consultations = adminService.getAllConsultations();
            log.info("Consultations fetched successfully. Total: {}", consultations.size());
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully", consultations));
        } catch (Exception e) {
            log.error("Error fetching consultations", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    /**
     * Get consultation overview
     */
    @GetMapping("/consultations/overview")
    public ResponseEntity<?> getConsultationOverview(Authentication authentication) {
        log.info("Fetching consultation overview");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get consultation overview");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing consultation overview", authentication.getName());
        try {
            ConsultationOverviewDTO overview = adminService.getConsultationOverview();
            log.info("Consultation overview fetched successfully");
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Consultation overview retrieved successfully", overview));
        } catch (Exception e) {
            log.error("Error fetching consultation overview", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    /**
     * Get specific consultation by ID
     */
    @GetMapping("/consultations/{id}")
    public ResponseEntity<?> getConsultationById(@PathVariable Long id, Authentication authentication) {
        log.info("Fetching consultation with id: {}", id);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get consultation by id");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing consultation with id: {}", authentication.getName(), id);
        try {
            Optional<Consultation> consultation = adminService.getConsultationById(id);
            if (consultation.isPresent()) {
                log.info("Consultation fetched successfully for id: {}", id);
                return ResponseEntity.ok().body(
                        new ApiResponse<>(HttpStatus.OK, "Consultation retrieved successfully", consultation.get()));
            } else {
                log.info("Consultation not found for id: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, "Consultation not found"));
            }
        } catch (Exception e) {
            log.error("Error fetching consultation for id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    // ==================== Activity Tracking Endpoints ====================

    /**
     * Get recent platform activities
     */
    @GetMapping("/activities/recent")
    public ResponseEntity<?> getRecentActivities(Authentication authentication) {
        log.info("Fetching recent activities");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get recent activities");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized access"));
        }
        log.info("Admin user {} accessing recent activities", authentication.getName());
        try {
            List<ActivityDTO> activities = adminService.getRecentActivities();
            log.info("Recent activities fetched successfully. Total: {}", activities.size());
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Recent activities retrieved successfully", activities));
        } catch (Exception e) {
            log.error("Error fetching recent activities", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

}
