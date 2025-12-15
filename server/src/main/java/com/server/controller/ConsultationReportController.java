package com.server.controller;

import com.server.dto.ConsultationReportDTO.ConsultationReportRequestDTO;
import com.server.dto.ConsultationReportDTO.ConsultationReportResponseDTO;
import com.server.dto.ConsultationReportDTO.ConsultationReportSummaryDTO;
import com.server.entity.Consultation;
import com.server.response.ApiResponse;
import com.server.service.ConsultationReportService;
import com.server.service.ConsultationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Consultation Report operations
 * Handles CRUD operations and queries for consultation reports
 */
@RestController
@RequestMapping("/api/v1/consultation-reports")
@Slf4j
public class ConsultationReportController {

    @Autowired
    private ConsultationReportService consultationReportService;

    @Autowired
    private ConsultationService consultationService;

    /**
     * Create a new consultation report
     * POST /api/v1/consultation-reports
     */
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody ConsultationReportRequestDTO requestDTO,
            Authentication authentication) {
        try {
            log.info("Request to create consultation report for consultation ID: {}", requestDTO.getConsultationId());

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to create consultation report");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            String username = authentication.getName();
            log.info("User {} attempting to create consultation report", username);

            // Verify the consultation exists and user has access
            Optional<Consultation> consultationOpt = consultationService
                    .getConsultationById(requestDTO.getConsultationId());
            if (consultationOpt.isEmpty()) {
                log.warn("Consultation not found with ID: {}", requestDTO.getConsultationId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultation not found"));
            }

            Consultation consultation = consultationOpt.get();

            // Verify consultant owns this consultation
            if (!consultation.getConsultant().getEmail().equals(username)) {
                log.warn("User {} not authorized to create report for consultation ID: {}", username,
                        requestDTO.getConsultationId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                "You are not authorized to create report for this consultation"));
            }

            Optional<ConsultationReportResponseDTO> createdReport = consultationReportService.createReport(requestDTO);

            if (createdReport.isPresent()) {
                log.info("Consultation report created successfully with ID: {}", createdReport.get().getId());
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ApiResponse<>(HttpStatus.CREATED, "Consultation report created successfully",
                                createdReport.get()));
            }

            log.error("Failed to create consultation report for consultation ID: {}", requestDTO.getConsultationId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create consultation report"));

        } catch (Exception e) {
            log.error("Error creating consultation report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error creating consultation report: " + e.getMessage()));
        }
    }

    /**
     * Update an existing consultation report
     * PUT /api/v1/consultation-reports/{reportId}
     */
    @PutMapping("/{reportId}")
    public ResponseEntity<?> updateReport(
            @PathVariable Long reportId,
            @RequestBody ConsultationReportRequestDTO requestDTO,
            Authentication authentication) {
        try {
            log.info("Request to update consultation report with ID: {}", reportId);

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to update consultation report");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            String username = authentication.getName();
            log.info("User {} attempting to update consultation report ID: {}", username, reportId);

            // Verify the report exists
            Optional<ConsultationReportResponseDTO> existingReport = consultationReportService.getReportById(reportId);
            if (existingReport.isEmpty()) {
                log.warn("Consultation report not found with ID: {}", reportId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultation report not found"));
            }

            // Verify consultant owns this report
            Optional<Consultation> consultationOpt = consultationService
                    .getConsultationById(existingReport.get().getConsultationId());
            if (consultationOpt.isPresent() && !consultationOpt.get().getConsultant().getEmail().equals(username)) {
                log.warn("User {} not authorized to update report ID: {}", username, reportId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(HttpStatus.FORBIDDEN, "You are not authorized to update this report"));
            }

            Optional<ConsultationReportResponseDTO> updatedReport = consultationReportService.updateReport(reportId,
                    requestDTO);

            if (updatedReport.isPresent()) {
                log.info("Consultation report updated successfully with ID: {}", reportId);
                return ResponseEntity.ok()
                        .body(new ApiResponse<>(HttpStatus.OK, "Consultation report updated successfully",
                                updatedReport.get()));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update consultation report"));

        } catch (Exception e) {
            log.error("Error updating consultation report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error updating consultation report: " + e.getMessage()));
        }
    }

    /**
     * Get a consultation report by ID
     * GET /api/v1/consultation-reports/{reportId}
     */
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReportById(
            @PathVariable Long reportId,
            Authentication authentication) {
        try {
            log.info("Request to get consultation report with ID: {}", reportId);

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to get consultation report");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            Optional<ConsultationReportResponseDTO> report = consultationReportService.getReportById(reportId);

            if (report.isPresent()) {
                log.info("Consultation report found with ID: {}", reportId);
                return ResponseEntity.ok()
                        .body(new ApiResponse<>(HttpStatus.OK, "Consultation report retrieved successfully",
                                report.get()));
            }

            log.warn("Consultation report not found with ID: {}", reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultation report not found"));

        } catch (Exception e) {
            log.error("Error fetching consultation report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching consultation report: " + e.getMessage()));
        }
    }

    /**
     * Get all reports for a specific consultation
     * GET /api/v1/consultation-reports/consultation/{consultationId}
     */
    @GetMapping("/consultation/{consultationId}")
    public ResponseEntity<?> getReportsByConsultationId(
            @PathVariable Long consultationId,
            Authentication authentication) {
        try {
            log.info("Request to get all reports for consultation ID: {}", consultationId);

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to get consultation reports");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            String username = authentication.getName();

            // Verify the consultation exists and user has access
            Optional<Consultation> consultationOpt = consultationService.getConsultationById(consultationId);
            if (consultationOpt.isEmpty()) {
                log.warn("Consultation not found with ID: {}", consultationId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultation not found"));
            }

            Consultation consultation = consultationOpt.get();

            // Verify user is either the consultant or farmer for this consultation
            boolean isAuthorized = consultation.getConsultant().getEmail().equals(username)
                    || consultation.getFarmer().getEmail().equals(username);

            if (!isAuthorized) {
                log.warn("User {} not authorized to view reports for consultation ID: {}", username, consultationId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                "You are not authorized to view reports for this consultation"));
            }

            List<ConsultationReportResponseDTO> reports = consultationReportService
                    .getReportsByConsultationId(consultationId);

            log.info("Found {} reports for consultation ID: {}", reports.size(), consultationId);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(HttpStatus.OK, "Consultation reports retrieved successfully", reports));

        } catch (Exception e) {
            log.error("Error fetching consultation reports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching consultation reports: " + e.getMessage()));
        }
    }

    /**
     * Get the latest report for a consultation
     * GET /api/v1/consultation-reports/consultation/{consultationId}/latest
     */
    @GetMapping("/consultation/{consultationId}/latest")
    public ResponseEntity<?> getLatestReportByConsultationId(
            @PathVariable Long consultationId,
            Authentication authentication) {
        try {
            log.info("Request to get latest report for consultation ID: {}", consultationId);

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to get latest consultation report");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            String username = authentication.getName();

            // Verify the consultation exists and user has access
            Optional<Consultation> consultationOpt = consultationService.getConsultationById(consultationId);
            if (consultationOpt.isEmpty()) {
                log.warn("Consultation not found with ID: {}", consultationId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultation not found"));
            }

            Consultation consultation = consultationOpt.get();

            // Verify user is either the consultant or farmer for this consultation
            boolean isAuthorized = consultation.getConsultant().getEmail().equals(username)
                    || consultation.getFarmer().getEmail().equals(username);

            if (!isAuthorized) {
                log.warn("User {} not authorized to view latest report for consultation ID: {}", username,
                        consultationId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                "You are not authorized to view reports for this consultation"));
            }

            Optional<ConsultationReportResponseDTO> report = consultationReportService
                    .getLatestReportByConsultationId(consultationId);

            if (report.isPresent()) {
                log.info("Latest report found for consultation ID: {}", consultationId);
                return ResponseEntity.ok()
                        .body(new ApiResponse<>(HttpStatus.OK, "Latest consultation report retrieved successfully",
                                report.get()));
            }

            log.warn("No reports found for consultation ID: {}", consultationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "No reports found for this consultation"));

        } catch (Exception e) {
            log.error("Error fetching latest consultation report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching latest consultation report: " + e.getMessage()));
        }
    }

    /**
     * Get all reports created by the authenticated consultant
     * GET /api/v1/consultation-reports/my-reports
     */
    @GetMapping("/my-reports")
    public ResponseEntity<?> getMyReports(Authentication authentication) {
        try {
            log.info("Request to get all reports for authenticated consultant");

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to get consultant reports");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            String username = authentication.getName();
            log.info("Fetching reports for consultant: {}", username);

            // This would require getting consultant ID from username
            // For now, returning a not implemented response
            // You'll need to add a method to get consultant by email and then use their ID

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ApiResponse<>(HttpStatus.NOT_IMPLEMENTED,
                            "Feature not yet implemented - requires consultant lookup"));

        } catch (Exception e) {
            log.error("Error fetching consultant reports: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching consultant reports: " + e.getMessage()));
        }
    }

    /**
     * Get upcoming follow-ups for the authenticated consultant
     * GET /api/v1/consultation-reports/upcoming-followups
     */
    @GetMapping("/upcoming-followups")
    public ResponseEntity<?> getUpcomingFollowUps(Authentication authentication) {
        try {
            log.info("Request to get upcoming follow-ups for authenticated consultant");

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to get upcoming follow-ups");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            String username = authentication.getName();
            log.info("Fetching upcoming follow-ups for consultant: {}", username);

            // This would require getting consultant ID from username
            // For now, returning a not implemented response

            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ApiResponse<>(HttpStatus.NOT_IMPLEMENTED,
                            "Feature not yet implemented - requires consultant lookup"));

        } catch (Exception e) {
            log.error("Error fetching upcoming follow-ups: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching upcoming follow-ups: " + e.getMessage()));
        }
    }

    /**
     * Get reports within a date range
     * GET /api/v1/consultation-reports/date-range?startDate=...&endDate=...
     */
    @GetMapping("/date-range")
    public ResponseEntity<?> getReportsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        try {
            log.info("Request to get reports between {} and {}", startDate, endDate);

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to get reports by date range");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            List<ConsultationReportSummaryDTO> reports = consultationReportService.getReportsByDateRange(startDate,
                    endDate);

            log.info("Found {} reports in the specified date range", reports.size());
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(HttpStatus.OK, "Reports retrieved successfully", reports));

        } catch (Exception e) {
            log.error("Error fetching reports by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching reports by date range: " + e.getMessage()));
        }
    }

    /**
     * Get report count for a consultation
     * GET /api/v1/consultation-reports/consultation/{consultationId}/count
     */
    @GetMapping("/consultation/{consultationId}/count")
    public ResponseEntity<?> getReportCount(
            @PathVariable Long consultationId,
            Authentication authentication) {
        try {
            log.info("Request to get report count for consultation ID: {}", consultationId);

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to get report count");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            Long count = consultationReportService.getReportCountByConsultationId(consultationId);

            log.info("Found {} reports for consultation ID: {}", count, consultationId);
            return ResponseEntity.ok()
                    .body(new ApiResponse<>(HttpStatus.OK, "Report count retrieved successfully", count));

        } catch (Exception e) {
            log.error("Error fetching report count: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching report count: " + e.getMessage()));
        }
    }

    /**
     * Delete a consultation report
     * DELETE /api/v1/consultation-reports/{reportId}
     */
    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long reportId,
            Authentication authentication) {
        try {
            log.info("Request to delete consultation report with ID: {}", reportId);

            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized access attempt to delete consultation report");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
            }

            String username = authentication.getName();
            log.info("User {} attempting to delete consultation report ID: {}", username, reportId);

            // Verify the report exists
            Optional<ConsultationReportResponseDTO> existingReport = consultationReportService.getReportById(reportId);
            if (existingReport.isEmpty()) {
                log.warn("Consultation report not found with ID: {}", reportId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultation report not found"));
            }

            // Verify consultant owns this report
            Optional<Consultation> consultationOpt = consultationService
                    .getConsultationById(existingReport.get().getConsultationId());
            if (consultationOpt.isPresent() && !consultationOpt.get().getConsultant().getEmail().equals(username)) {
                log.warn("User {} not authorized to delete report ID: {}", username, reportId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(HttpStatus.FORBIDDEN, "You are not authorized to delete this report"));
            }

            boolean deleted = consultationReportService.deleteReport(reportId);

            if (deleted) {
                log.info("Consultation report deleted successfully with ID: {}", reportId);
                return ResponseEntity.ok()
                        .body(new ApiResponse<>(HttpStatus.OK, "Consultation report deleted successfully"));
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete consultation report"));

        } catch (Exception e) {
            log.error("Error deleting consultation report: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error deleting consultation report: " + e.getMessage()));
        }
    }
}
