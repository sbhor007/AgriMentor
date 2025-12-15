package com.server.controller;

import com.server.dto.ConsultationDTO.ConsultationResponse;
import com.server.entity.Consultation;
import com.server.entity.Consultant;
import com.server.entity.Farmer;
import com.server.enumeration.ConsultationRequestStatus;
import com.server.response.ApiResponse;
import com.server.service.ConsultantService;
import com.server.service.ConsultationService;
import com.server.service.FarmerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Consultation operations
 * Handles CRUD operations, status management, statistics, and validation for
 * consultations
 */
@RestController
@RequestMapping("/api/v1/consultations")
@Slf4j
public class ConsultationController {

        @Autowired
        private ConsultationService consultationService;

        @Autowired
        private FarmerService farmerService;

        @Autowired
        private ConsultantService consultantService;

        // ==================== Consultation Retrieval Endpoints ====================

        /**
         * Get consultation by ID
         * GET /api/v1/consultations/{id}
         */
        @GetMapping("/{id}")
        public ResponseEntity<?> getConsultationById(
                        @PathVariable Long id,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultation with ID: {}", id);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt to get consultation");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();
                        Optional<Consultation> consultationOpt = consultationService.getConsultationById(id);

                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Verify user is either the farmer or consultant for this consultation
                        boolean isAuthorized = consultation.getFarmer().getEmail().equals(username)
                                        || consultation.getConsultant().getEmail().equals(username);

                        if (!isAuthorized) {
                                log.warn("User {} not authorized to view consultation ID: {}", username, id);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view this consultation"));
                        }

                        log.info("Consultation found with ID: {}", id);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultation retrieved successfully",
                                                        consultation));

                } catch (Exception e) {
                        log.error("Error fetching consultation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultation: " + e.getMessage()));
                }
        }

        /**
         * Get all consultations for a farmer
         * GET /api/v1/consultations/farmer/{farmerId}
         */
        @GetMapping("/farmer/{farmerId}")
        public ResponseEntity<?> getConsultationsByFarmerId(
                        @PathVariable Long farmerId,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations for farmer ID: {}", farmerId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify the farmer exists and user has access
                        Optional<Farmer> farmerOpt = farmerService.findFarmerEntityById(farmerId);
                        if (farmerOpt.isEmpty()) {
                                log.warn("Farmer not found with ID: {}", farmerId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farmer not found"));
                        }

                        // Only the farmer themselves can view their consultations
                        Farmer farmer = farmerOpt.get();
                        if (!farmer.getEmail().equals(username)) {
                                log.warn("User {} not authorized to view farmer {} consultations", username, farmerId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view these consultations"));
                        }

                        List<Consultation> consultations = consultationService.getConsultationsByFarmerId(farmerId);

                        log.info("Found {} consultations for farmer ID: {}", consultations.size(), farmerId);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get all consultations for a consultant
         * GET /api/v1/consultations/consultant/{consultantId}
         */
        @GetMapping("/consultant/{consultantId}")
        public ResponseEntity<?> getConsultationsByConsultantId(
                        @PathVariable Long consultantId,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations for consultant ID: {}", consultantId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify the consultant exists and user has access
                        Optional<Consultant> consultantOpt = consultantService.getConsultantById(consultantId);
                        if (consultantOpt.isEmpty()) {
                                log.warn("Consultant not found with ID: {}", consultantId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultant not found"));
                        }

                        // Only the consultant themselves can view their consultations
                        if (!consultantOpt.get().getEmail().equals(username)) {
                                log.warn("User {} not authorized to view consultant {} consultations", username,
                                                consultantId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view these consultations"));
                        }

                        List<Consultation> consultations = consultationService
                                        .getConsultationsByConsultantId(consultantId);

                        log.info("Found {} consultations for consultant ID: {}", consultations.size(), consultantId);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get consultations by crop
         * GET /api/v1/consultations/crop/{cropId}
         */
        @GetMapping("/crop/{cropId}")
        public ResponseEntity<?> getConsultationsByCropId(
                        @PathVariable Long cropId,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations for crop ID: {}", cropId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        List<Consultation> consultations = consultationService.getConsultationsByCropId(cropId);

                        log.info("Found {} consultations for crop ID: {}", consultations.size(), cropId);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get top 10 recent consultations
         * GET /api/v1/consultations/recent
         */
        @GetMapping("/recent")
        public ResponseEntity<?> getRecentConsultations(Authentication authentication) {
                try {
                        log.info("Request to get recent consultations");

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        List<Consultation> consultations = consultationService.getRecentConsultations();

                        log.info("Found {} recent consultations", consultations.size());
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK,
                                                        "Recent consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching recent consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching recent consultations: " + e.getMessage()));
                }
        }

        // ==================== Status-Based Retrieval Endpoints ====================

        /**
         * Get consultations by status
         * GET /api/v1/consultations/status/{status}
         */
        @GetMapping("/status/{status}")
        public ResponseEntity<?> getConsultationsByStatus(
                        @PathVariable ConsultationRequestStatus status,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations with status: {}", status);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        List<Consultation> consultations = consultationService.getConsultationsByStatus(status);

                        log.info("Found {} consultations with status: {}", consultations.size(), status);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations by status: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get farmer's consultations by status
         * GET /api/v1/consultations/farmer/{farmerId}/status/{status}
         */
        @GetMapping("/farmer/{farmerId}/status/{status}")
        public ResponseEntity<?> getConsultationsByFarmerAndStatus(
                        @PathVariable Long farmerId,
                        @PathVariable ConsultationRequestStatus status,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations for farmer {} with status: {}", farmerId, status);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify farmer exists and user has access
                        Optional<Farmer> farmerOpt = farmerService.findFarmerEntityById(farmerId);
                        if (farmerOpt.isEmpty()) {
                                log.warn("Farmer not found with ID: {}", farmerId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farmer not found"));
                        }

                        if (!farmerOpt.get().getEmail().equals(username)) {
                                log.warn("User {} not authorized to view farmer {} consultations", username, farmerId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view these consultations"));
                        }

                        List<Consultation> consultations = consultationService
                                        .getConsultationsByFarmerAndStatus(farmerId, status);

                        log.info("Found {} consultations for farmer {} with status: {}",
                                        consultations.size(), farmerId, status);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get consultant's consultations by status
         * GET /api/v1/consultations/consultant/{consultantId}/status/{status}
         */
        @GetMapping("/consultant/{consultantId}/status/{status}")
        public ResponseEntity<?> getConsultationsByConsultantAndStatus(
                        @PathVariable Long consultantId,
                        @PathVariable ConsultationRequestStatus status,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations for consultant {} with status: {}", consultantId,
                                        status);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultant exists and user has access
                        Optional<Consultant> consultantOpt = consultantService.getConsultantById(consultantId);
                        if (consultantOpt.isEmpty()) {
                                log.warn("Consultant not found with ID: {}", consultantId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultant not found"));
                        }

                        if (!consultantOpt.get().getEmail().equals(username)) {
                                log.warn("User {} not authorized to view consultant {} consultations", username,
                                                consultantId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view these consultations"));
                        }

                        List<Consultation> consultations = consultationService
                                        .getConsultationsByConsultantAndStatus(consultantId, status);

                        log.info("Found {} consultations for consultant {} with status: {}",
                                        consultations.size(), consultantId, status);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get consultations with multiple statuses
         * GET /api/v1/consultations/status/multiple?statuses=PENDING,APPROVED
         */
        @GetMapping("/status/multiple")
        public ResponseEntity<?> getConsultationsByMultipleStatuses(
                        @RequestParam String statuses,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations with statuses: {}", statuses);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        List<ConsultationRequestStatus> statusList = Arrays.stream(statuses.split(","))
                                        .map(String::trim)
                                        .map(ConsultationRequestStatus::valueOf)
                                        .collect(Collectors.toList());

                        List<Consultation> consultations = consultationService.getConsultationsByStatusIn(statusList);

                        log.info("Found {} consultations with statuses: {}", consultations.size(), statuses);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid status value: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST,
                                                        "Invalid status value. Valid values: PENDING, APPROVED, REJECTED, COMPLETED"));
                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get farmer's consultations with multiple statuses
         * GET
         * /api/v1/consultations/farmer/{farmerId}/status/multiple?statuses=PENDING,APPROVED
         */
        @GetMapping("/farmer/{farmerId}/status/multiple")
        public ResponseEntity<?> getConsultationsByFarmerAndMultipleStatuses(
                        @PathVariable Long farmerId,
                        @RequestParam String statuses,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations for farmer {} with statuses: {}", farmerId, statuses);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify farmer exists and user has access
                        Optional<Farmer> farmerOpt = farmerService.findFarmerEntityById(farmerId);
                        if (farmerOpt.isEmpty()) {
                                log.warn("Farmer not found with ID: {}", farmerId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farmer not found"));
                        }

                        Farmer farmer = farmerOpt.get();
                        if (!farmer.getEmail().equals(username)) {
                                log.warn("User {} not authorized to view farmer {} consultations", username, farmerId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view these consultations"));
                        }

                        List<ConsultationRequestStatus> statusList = Arrays.stream(statuses.split(","))
                                        .map(String::trim)
                                        .map(ConsultationRequestStatus::valueOf)
                                        .collect(Collectors.toList());

                        List<Consultation> consultations = consultationService
                                        .getConsultationsByFarmerAndStatusIn(farmerId, statusList);

                        log.info("Found {} consultations for farmer {} with statuses: {}",
                                        consultations.size(), farmerId, statuses);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid status value: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST,
                                                        "Invalid status value. Valid values: PENDING, APPROVED, REJECTED, COMPLETED"));
                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get consultant's consultations with multiple statuses
         * GET
         * /api/v1/consultations/consultant/{consultantId}/status/multiple?statuses=PENDING,APPROVED
         */
        @GetMapping("/consultant/{consultantId}/status/multiple")
        public ResponseEntity<?> getConsultationsByConsultantAndMultipleStatuses(
                        @PathVariable Long consultantId,
                        @RequestParam String statuses,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations for consultant {} with statuses: {}", consultantId,
                                        statuses);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultant exists and user has access
                        Optional<Consultant> consultantOpt = consultantService.getConsultantById(consultantId);
                        if (consultantOpt.isEmpty()) {
                                log.warn("Consultant not found with ID: {}", consultantId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Consultant not found"));
                        }

                        if (!consultantOpt.get().getEmail().equals(username)) {
                                log.warn("User {} not authorized to view consultant {} consultations", username,
                                                consultantId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view these consultations"));
                        }

                        List<ConsultationRequestStatus> statusList = Arrays.stream(statuses.split(","))
                                        .map(String::trim)
                                        .map(ConsultationRequestStatus::valueOf)
                                        .collect(Collectors.toList());

                        List<Consultation> consultations = consultationService
                                        .getConsultationsByConsultantAndStatusIn(consultantId, statusList);

                        log.info("Found {} consultations for consultant {} with statuses: {}",
                                        consultations.size(), consultantId, statuses);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid status value: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST,
                                                        "Invalid status value. Valid values: PENDING, APPROVED, REJECTED, COMPLETED"));
                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        // ==================== Date-Based Retrieval Endpoints ====================

        /**
         * Get consultations within a date range
         * GET /api/v1/consultations/date-range?startDate=...&endDate=...
         */
        @GetMapping("/date-range")
        public ResponseEntity<?> getConsultationsByDateRange(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations between {} and {}", startDate, endDate);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        List<Consultation> consultations = consultationService
                                        .getConsultationsByDateRange(startDate, endDate);

                        log.info("Found {} consultations in date range", consultations.size());
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations by date range: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get consultations created after a specific date
         * GET /api/v1/consultations/after-date?date=...
         */
        @GetMapping("/after-date")
        public ResponseEntity<?> getConsultationsAfterDate(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations created after {}", date);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        List<Consultation> consultations = consultationService.getConsultationsAfterDate(date);

                        log.info("Found {} consultations created after {}", consultations.size(), date);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations after date: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        /**
         * Get consultations by status created after a specific date
         * GET /api/v1/consultations/status/{status}/after-date?date=...
         */
        @GetMapping("/status/{status}/after-date")
        public ResponseEntity<?> getConsultationsByStatusAndDateAfter(
                        @PathVariable ConsultationRequestStatus status,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultations with status {} created after {}", status, date);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        List<Consultation> consultations = consultationService
                                        .getConsultationsByStatusAndDateAfter(status, date);

                        log.info("Found {} consultations with status {} created after {}",
                                        consultations.size(), status, date);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Consultations retrieved successfully",
                                                        consultations));

                } catch (Exception e) {
                        log.error("Error fetching consultations: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching consultations: " + e.getMessage()));
                }
        }

        // ==================== Status Management Endpoints ====================

        /**
         * Approve a consultation
         * PUT /api/v1/consultations/{id}/approve
         */
        @PutMapping("/{id}/approve")
        public ResponseEntity<?> approveConsultation(
                        @PathVariable Long id,
                        Authentication authentication) {
                try {
                        log.info("Request to approve consultation ID: {}", id);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultation exists
                        Optional<Consultation> consultationOpt = consultationService.getConsultationById(id);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Only the assigned consultant can approve
                        if (!consultation.getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to approve consultation ID: {}", username, id);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can approve this consultation"));
                        }

                        Optional<Consultation> approved = consultationService.approveConsultation(id);

                        if (approved.isPresent()) {
                                log.info("Consultation ID {} approved successfully", id);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Consultation approved successfully",
                                                                approved.get()));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to approve consultation"));

                } catch (RuntimeException e) {
                        log.error("Error approving consultation: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error approving consultation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error approving consultation: " + e.getMessage()));
                }
        }

        /**
         * Reject a consultation
         * PUT /api/v1/consultations/{id}/reject
         */
        @PutMapping("/{id}/reject")
        public ResponseEntity<?> rejectConsultation(
                        @PathVariable Long id,
                        Authentication authentication) {
                try {
                        log.info("Request to reject consultation ID: {}", id);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultation exists
                        Optional<Consultation> consultationOpt = consultationService.getConsultationById(id);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Only the assigned consultant can reject
                        if (!consultation.getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to reject consultation ID: {}", username, id);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can reject this consultation"));
                        }

                        Optional<Consultation> rejected = consultationService.rejectConsultation(id);

                        if (rejected.isPresent()) {
                                log.info("Consultation ID {} rejected successfully", id);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Consultation rejected successfully",
                                                                rejected.get()));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to reject consultation"));

                } catch (RuntimeException e) {
                        log.error("Error rejecting consultation: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error rejecting consultation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error rejecting consultation: " + e.getMessage()));
                }
        }

        /**
         * Complete a consultation
         * PUT /api/v1/consultations/{id}/complete
         */
        @PutMapping("/{id}/complete")
        public ResponseEntity<?> completeConsultation(
                        @PathVariable Long id,
                        Authentication authentication) {
                try {
                        log.info("Request to complete consultation ID: {}", id);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultation exists
                        Optional<Consultation> consultationOpt = consultationService.getConsultationById(id);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Only the assigned consultant can complete
                        if (!consultation.getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to complete consultation ID: {}", username, id);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can complete this consultation"));
                        }

                        Optional<Consultation> completed = consultationService.completeConsultation(id);

                        if (completed.isPresent()) {
                                log.info("Consultation ID {} completed successfully", id);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Consultation completed successfully",
                                                                completed.get()));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to complete consultation"));

                } catch (RuntimeException e) {
                        log.error("Error completing consultation: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error completing consultation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error completing consultation: " + e.getMessage()));
                }
        }

        /**
         * Update consultation status
         * PUT /api/v1/consultations/{id}/status
         * Request body: { "status": "APPROVED" }
         */
        @PutMapping("/{id}/status")
        public ResponseEntity<?> updateConsultationStatus(
                        @PathVariable Long id,
                        @RequestBody UpdateStatusRequest request,
                        Authentication authentication) {
                try {
                        log.info("Request to update consultation ID {} status to {}", id, request.getStatus());

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultation exists
                        Optional<Consultation> consultationOpt = consultationService.getConsultationById(id);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Only the assigned consultant can update status
                        if (!consultation.getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to update consultation ID: {}", username, id);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can update this consultation"));
                        }

                        Optional<Consultation> updated = consultationService
                                        .updateConsultationStatus(id, request.getStatus());

                        if (updated.isPresent()) {
                                log.info("Consultation ID {} status updated to {}", id, request.getStatus());
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Consultation status updated successfully",
                                                                updated.get()));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to update consultation status"));

                } catch (Exception e) {
                        log.error("Error updating consultation status: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error updating consultation status: " + e.getMessage()));
                }
        }

        // ==================== Update Endpoints ====================

        /**
         * Update consultation topic and description
         * PUT /api/v1/consultations/{id}
         * Request body: { "topic": "...", "description": "..." }
         */
        @PutMapping("/{id}")
        public ResponseEntity<?> updateConsultation(
                        @PathVariable Long id,
                        @RequestBody UpdateConsultationRequest request,
                        Authentication authentication) {
                try {
                        log.info("Request to update consultation ID: {}", id);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultation exists
                        Optional<Consultation> consultationOpt = consultationService.getConsultationById(id);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Only the farmer who created it can update
                        if (!consultation.getFarmer().getEmail().equals(username)) {
                                log.warn("User {} not authorized to update consultation ID: {}", username, id);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the farmer who created this consultation can update it"));
                        }

                        Optional<Consultation> updated = consultationService
                                        .updateConsultation(id, request.getTopic(), request.getDescription());

                        if (updated.isPresent()) {
                                log.info("Consultation ID {} updated successfully", id);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Consultation updated successfully",
                                                                updated.get()));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to update consultation"));

                } catch (Exception e) {
                        log.error("Error updating consultation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error updating consultation: " + e.getMessage()));
                }
        }

        // ==================== Statistics Endpoints ====================

        /**
         * Get total consultation count for farmer
         * GET /api/v1/consultations/stats/farmer/{farmerId}/count
         */
        @GetMapping("/stats/farmer/{farmerId}/count")
        public ResponseEntity<?> getFarmerConsultationCount(
                        @PathVariable Long farmerId,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultation count for farmer ID: {}", farmerId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        Long count = consultationService.getConsultationCountByFarmer(farmerId);

                        log.info("Farmer {} has {} consultations", farmerId, count);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Count retrieved successfully", count));

                } catch (Exception e) {
                        log.error("Error fetching consultation count: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching count: " + e.getMessage()));
                }
        }

        /**
         * Get total consultation count for consultant
         * GET /api/v1/consultations/stats/consultant/{consultantId}/count
         */
        @GetMapping("/stats/consultant/{consultantId}/count")
        public ResponseEntity<?> getConsultantConsultationCount(
                        @PathVariable Long consultantId,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultation count for consultant ID: {}", consultantId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        Long count = consultationService.getConsultationCountByConsultant(consultantId);

                        log.info("Consultant {} has {} consultations", consultantId, count);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Count retrieved successfully", count));

                } catch (Exception e) {
                        log.error("Error fetching consultation count: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching count: " + e.getMessage()));
                }
        }

        /**
         * Get consultation count by status
         * GET /api/v1/consultations/stats/status/{status}/count
         */
        @GetMapping("/stats/status/{status}/count")
        public ResponseEntity<?> getConsultationCountByStatus(
                        @PathVariable ConsultationRequestStatus status,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultation count for status: {}", status);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        Long count = consultationService.getConsultationCountByStatus(status);

                        log.info("Found {} consultations with status: {}", count, status);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Count retrieved successfully", count));

                } catch (Exception e) {
                        log.error("Error fetching consultation count: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching count: " + e.getMessage()));
                }
        }

        /**
         * Get consultant's consultation count by status
         * GET
         * /api/v1/consultations/stats/consultant/{consultantId}/status/{status}/count
         */
        @GetMapping("/stats/consultant/{consultantId}/status/{status}/count")
        public ResponseEntity<?> getConsultantConsultationCountByStatus(
                        @PathVariable Long consultantId,
                        @PathVariable ConsultationRequestStatus status,
                        Authentication authentication) {
                try {
                        log.info("Request to get count for consultant {} with status: {}", consultantId, status);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        Long count = consultationService
                                        .getConsultationCountByConsultantAndStatus(consultantId, status);

                        log.info("Consultant {} has {} consultations with status: {}", consultantId, count, status);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Count retrieved successfully", count));

                } catch (Exception e) {
                        log.error("Error fetching consultation count: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching count: " + e.getMessage()));
                }
        }

        /**
         * Get farmer's consultation count by status
         * GET /api/v1/consultations/stats/farmer/{farmerId}/status/{status}/count
         */
        @GetMapping("/stats/farmer/{farmerId}/status/{status}/count")
        public ResponseEntity<?> getFarmerConsultationCountByStatus(
                        @PathVariable Long farmerId,
                        @PathVariable ConsultationRequestStatus status,
                        Authentication authentication) {
                try {
                        log.info("Request to get count for farmer {} with status: {}", farmerId, status);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        Long count = consultationService
                                        .getConsultationCountByFarmerAndStatus(farmerId, status);

                        log.info("Farmer {} has {} consultations with status: {}", farmerId, count, status);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Count retrieved successfully", count));

                } catch (Exception e) {
                        log.error("Error fetching consultation count: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching count: " + e.getMessage()));
                }
        }

        /**
         * Get consultation count by crop
         * GET /api/v1/consultations/stats/crop/{cropId}/count
         */
        @GetMapping("/stats/crop/{cropId}/count")
        public ResponseEntity<?> getConsultationCountByCrop(
                        @PathVariable Long cropId,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultation count for crop ID: {}", cropId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        Long count = consultationService.getConsultationCountByCrop(cropId);

                        log.info("Crop {} has {} consultations", cropId, count);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Count retrieved successfully", count));

                } catch (Exception e) {
                        log.error("Error fetching consultation count: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching count: " + e.getMessage()));
                }
        }

        /**
         * Get consultation count after specific date
         * GET /api/v1/consultations/stats/after-date/count?date=...
         */
        @GetMapping("/stats/after-date/count")
        public ResponseEntity<?> getConsultationCountAfterDate(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
                        Authentication authentication) {
                try {
                        log.info("Request to get consultation count after {}", date);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        Long count = consultationService.getConsultationCountAfterDate(date);

                        log.info("Found {} consultations created after {}", count, date);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Count retrieved successfully", count));

                } catch (Exception e) {
                        log.error("Error fetching consultation count: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching count: " + e.getMessage()));
                }
        }

        // ==================== Validation Endpoints ====================

        /**
         * Check if active consultation exists between farmer and consultant
         * GET /api/v1/consultations/validate/active?farmerId=...&consultantId=...
         */
        @GetMapping("/validate/active")
        public ResponseEntity<?> hasActiveConsultation(
                        @RequestParam Long farmerId,
                        @RequestParam Long consultantId,
                        Authentication authentication) {
                try {
                        log.info("Request to check active consultation between farmer {} and consultant {}",
                                        farmerId, consultantId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        boolean hasActive = consultationService.hasActiveConsultation(farmerId, consultantId);

                        log.info("Active consultation exists: {}", hasActive);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Validation completed", hasActive));

                } catch (Exception e) {
                        log.error("Error validating active consultation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error validating: " + e.getMessage()));
                }
        }

        /**
         * Validate if consultation can be created
         * GET /api/v1/consultations/validate/can-create?farmerId=...&consultantId=...
         */
        @GetMapping("/validate/can-create")
        public ResponseEntity<?> canCreateConsultation(
                        @RequestParam Long farmerId,
                        @RequestParam Long consultantId,
                        Authentication authentication) {
                try {
                        log.info("Request to validate if consultation can be created between farmer {} and consultant {}",
                                        farmerId, consultantId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        boolean canCreate = consultationService.canCreateConsultation(farmerId, consultantId);

                        log.info("Can create consultation: {}", canCreate);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Validation completed", canCreate));

                } catch (Exception e) {
                        log.error("Error validating consultation creation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error validating: " + e.getMessage()));
                }
        }

        // ==================== Delete Endpoint ====================

        /**
         * Delete a consultation
         * DELETE /api/v1/consultations/{id}
         */
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteConsultation(
                        @PathVariable Long id,
                        Authentication authentication) {
                try {
                        log.info("Request to delete consultation ID: {}", id);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify consultation exists
                        Optional<Consultation> consultationOpt = consultationService.getConsultationById(id);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", id);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Only the farmer who created it can delete
                        if (!consultation.getFarmer().getEmail().equals(username)) {
                                log.warn("User {} not authorized to delete consultation ID: {}", username, id);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the farmer who created this consultation can delete it"));
                        }

                        boolean deleted = consultationService.deleteConsultation(id);

                        if (deleted) {
                                log.info("Consultation ID {} deleted successfully", id);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Consultation deleted successfully"));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to delete consultation"));

                } catch (Exception e) {
                        log.error("Error deleting consultation: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error deleting consultation: " + e.getMessage()));
                }
        }

        // ==================== Inner Classes for Request Bodies ====================

        /**
         * Request DTO for updating consultation status
         */
        public static class UpdateStatusRequest {
                private ConsultationRequestStatus status;

                public ConsultationRequestStatus getStatus() {
                        return status;
                }

                public void setStatus(ConsultationRequestStatus status) {
                        this.status = status;
                }
        }

        /**
         * Request DTO for updating consultation
         */
        public static class UpdateConsultationRequest {
                private String topic;
                private String description;

                public String getTopic() {
                        return topic;
                }

                public void setTopic(String topic) {
                        this.topic = topic;
                }

                public String getDescription() {
                        return description;
                }

                public void setDescription(String description) {
                        this.description = description;
                }
        }
}
