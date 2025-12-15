package com.server.service;

import com.server.dto.adminDTO.*;
import com.server.entity.Consultation;
import com.server.entity.User;
import com.server.enumeration.ConsultationRequestStatus;
import com.server.enumeration.Role;

import com.server.repository.ConsultationRepository;
import com.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AdminService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ConsultationRepository consultationRepository;

    public boolean rejectConsultant(String username) {
        try {
            if (!consultantService.deleteConsultant(username)) {
                log.error("Failed to delete consultant with username: {}", username);
                return false;
            }

            String emailBody = "Dear " + username + ",\n\n" +
                    "We regret to inform you that your consultant application has been rejected.\n" +
                    "For further details, please contact our support team. and  Register Again\n\n" +
                    "Best regards,\n" +
                    "The Admin Team";
            emailService.sendEmail("Consultant Application Rejected", emailBody);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get comprehensive dashboard statistics
     */
    public AdminDashboardStatsDTO getDashboardStatistics() {
        log.info("Fetching dashboard statistics");

        AdminDashboardStatsDTO stats = new AdminDashboardStatsDTO();

        // User statistics
        Long totalUsers = userRepository.count();
        Long totalFarmers = userRepository.countByRole(Role.FARMER);
        Long totalConsultants = userRepository.countByRole(Role.CONSULTANT);

        stats.setTotalUsers(totalUsers);
        stats.setTotalFarmers(totalFarmers);
        stats.setTotalConsultants(totalConsultants);

        // Consultation statistics
        Long activeConsultations = consultationRepository
                .countByConsultationRequestStatus(ConsultationRequestStatus.APPROVED);
        Long approvedConsultations = consultationRepository
                .countByConsultationRequestStatus(ConsultationRequestStatus.APPROVED);
        Long inProgressConsultations = activeConsultations; // Assuming approved = in progress
        Long pendingRequests = consultationRepository
                .countByConsultationRequestStatus(ConsultationRequestStatus.PENDING);

        stats.setActiveConsultations(activeConsultations);
        stats.setApprovedConsultations(approvedConsultations);
        stats.setInProgressConsultations(inProgressConsultations);
        stats.setPendingRequests(pendingRequests);

        // Platform activity rate (percentage of active users)
        Long activeUsers = userRepository.countByIsActive(true);
        Double activityRate = totalUsers > 0 ? (activeUsers.doubleValue() / totalUsers.doubleValue()) * 100 : 0.0;
        stats.setPlatformActivityRate(Math.round(activityRate * 10.0) / 10.0); // Round to 1 decimal

        // User growth statistics
        stats.setUserGrowth(getUserGrowthStats());

        log.info("Dashboard statistics fetched successfully");
        return stats;
    }

    /**
     * Get user growth statistics
     */
    private UserGrowthStats getUserGrowthStats() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        LocalDateTime twoMonthsAgo = LocalDateTime.now().minus(2, ChronoUnit.MONTHS);

        // Current month counts
        Long farmersThisMonth = userRepository.countByRoleAndCreatedAtAfter(Role.FARMER, oneMonthAgo);
        Long consultantsThisMonth = userRepository.countByRoleAndCreatedAtAfter(Role.CONSULTANT, oneMonthAgo);

        // Previous month counts
        Long farmersLastMonth = userRepository.countByRoleAndCreatedAtAfter(Role.FARMER, twoMonthsAgo)
                - farmersThisMonth;
        Long consultantsLastMonth = userRepository.countByRoleAndCreatedAtAfter(Role.CONSULTANT, twoMonthsAgo)
                - consultantsThisMonth;

        // Calculate growth percentages
        Double farmerGrowth = farmersLastMonth > 0
                ? ((double) (farmersThisMonth - farmersLastMonth) / farmersLastMonth) * 100
                : 0.0;
        Double consultantGrowth = consultantsLastMonth > 0
                ? ((double) (consultantsThisMonth - consultantsLastMonth) / consultantsLastMonth) * 100
                : 0.0;

        return new UserGrowthStats(
                farmersThisMonth,
                consultantsThisMonth,
                Math.round(farmerGrowth * 10.0) / 10.0,
                Math.round(consultantGrowth * 10.0) / 10.0);
    }

    /**
     * Get detailed user statistics
     */
    public UserStatisticsDTO getUserStatistics() {
        log.info("Fetching user statistics");

        UserStatisticsDTO stats = new UserStatisticsDTO();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalFarmers(userRepository.countByRole(Role.FARMER));
        stats.setTotalConsultants(userRepository.countByRole(Role.CONSULTANT));
        stats.setTotalAdmins(userRepository.countByRole(Role.ADMIN));
        stats.setActiveUsers(userRepository.countByIsActive(true));
        stats.setInactiveUsers(userRepository.countByIsActive(false));
        stats.setVerifiedUsers(userRepository.countByIsVerified(true));
        stats.setUnverifiedUsers(userRepository.countByIsVerified(false));

        log.info("User statistics fetched successfully");
        return stats;
    }

    /**
     * Get consultation overview statistics
     */
    public ConsultationOverviewDTO getConsultationOverview() {
        log.info("Fetching consultation overview");

        ConsultationOverviewDTO overview = new ConsultationOverviewDTO();

        overview.setTotalConsultations(consultationRepository.count());
        overview.setActiveConsultations(
                consultationRepository.countByConsultationRequestStatus(ConsultationRequestStatus.APPROVED));
        overview.setCompletedConsultations(
                consultationRepository.countByConsultationRequestStatus(ConsultationRequestStatus.COMPLETED));
        overview.setPendingConsultations(
                consultationRepository.countByConsultationRequestStatus(ConsultationRequestStatus.PENDING));
        overview.setRejectedConsultations(
                consultationRepository.countByConsultationRequestStatus(ConsultationRequestStatus.REJECTED));
        overview.setApprovedConsultations(
                consultationRepository.countByConsultationRequestStatus(ConsultationRequestStatus.APPROVED));

        log.info("Consultation overview fetched successfully");
        return overview;
    }

    /**
     * Get all users with optional role filtering
     */
    public List<User> getAllUsers(Role role) {
        log.info("Fetching all users with role filter: {}", role);

        if (role != null) {
            return userRepository.findByRole(role);
        }
        return userRepository.findAll();
    }

    /**
     * Get all farmers
     */
    public List<User> getAllFarmers() {
        log.info("Fetching all farmers");
        return userRepository.findByRole(Role.FARMER);
    }

    /**
     * Get all consultations
     */
    public List<Consultation> getAllConsultations() {
        log.info("Fetching all consultations");
        return consultationRepository.findAll();
    }

    /**
     * Get recent platform activities
     */
    public List<ActivityDTO> getRecentActivities() {
        log.info("Fetching recent activities");

        List<ActivityDTO> activities = new ArrayList<>();

        // Get recent users
        List<User> recentUsers = userRepository.findTop10ByOrderByCreatedAtDesc();
        for (User user : recentUsers) {
            ActivityDTO activity = new ActivityDTO();
            activity.setType("USER_REGISTERED");
            activity.setDescription("New " + user.getRole().toString().toLowerCase() + " registered");
            activity.setUsername(user.getEmail());
            activity.setUserRole(user.getRole().toString());
            activity.setTimestamp(user.getCreatedAt());
            activities.add(activity);
        }

        // Get recent consultations
        List<Consultation> recentConsultations = consultationRepository.findTop10ByOrderByCreatedAtDesc();
        for (Consultation consultation : recentConsultations) {
            ActivityDTO activity = new ActivityDTO();

            if (consultation.getConsultationRequestStatus() == ConsultationRequestStatus.APPROVED) {
                activity.setType("CONSULTATION_APPROVED");
                activity.setDescription("Consultation approved: " + consultation.getTopic());
            } else if (consultation.getConsultationRequestStatus() == ConsultationRequestStatus.PENDING) {
                activity.setType("CONSULTATION_CREATED");
                activity.setDescription("New consultation request: " + consultation.getTopic());
            } else {
                activity.setType("CONSULTATION_" + consultation.getConsultationRequestStatus().toString());
                activity.setDescription(
                        "Consultation " + consultation.getConsultationRequestStatus().toString().toLowerCase() + ": "
                                + consultation.getTopic());
            }

            if (consultation.getFarmer() != null) {
                activity.setUsername(consultation.getFarmer().getEmail());
            } else {
                activity.setUsername("Unknown Farmer");
            }
            activity.setUserRole("FARMER");
            activity.setTimestamp(consultation.getCreatedAt());
            activities.add(activity);
        }

        // Sort by timestamp descending
        activities.sort((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()));

        // Return top 10
        log.info("Recent activities fetched successfully. Total: {}", activities.size());
        return activities.stream().limit(10).toList();
    }

    /**
     * Toggle user active status
     */
    public boolean toggleUserStatus(String username) {
        log.info("Toggling user status for: {}", username);

        Optional<User> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            log.error("User not found: {}", username);
            return false;
        }

        User user = userOpt.get();
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        log.info("User status toggled successfully for: {}. New status: {}", username, user.getIsActive());
        return true;
    }

    /**
     * Get consultation by ID
     */
    public Optional<Consultation> getConsultationById(Long id) {
        log.info("Fetching consultation with id: {}", id);
        return consultationRepository.findById(id);
    }
}
