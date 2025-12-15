package com.server.controller;

import com.server.dto.AddressDTO;
import com.server.dto.FarmVisitRequest;
import com.server.dto.FarmVisitResponse;
import com.server.entity.Address;
import com.server.entity.Consultation;
import com.server.entity.Farmvisit;
import com.server.enumeration.VisitStatus;
import com.server.response.ApiResponse;
import com.server.service.ConsultationService;
import com.server.service.FarmvisitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST Controller for Farm Visit operations
 * Handles scheduling, rescheduling, status management, and CRUD operations for
 * farm visits
 */
@RestController
@RequestMapping("/api/v1/farmvisits")
// @CrossOrigin(origins = "*")
@Slf4j
public class FarmVisitController {

        @Autowired
        private FarmvisitService farmvisitService;

        @Autowired
        private ConsultationService consultationService;

        // ==================== Farm Visit Scheduling Endpoints ====================

        /**
         * Schedule a new farm visit for a consultation
         * POST /api/v1/farmvisits/schedule/{consultationId}
         */
        @PostMapping("/schedule/{consultationId}")
        public ResponseEntity<?> scheduleFarmVisit(
                        @PathVariable Long consultationId,
                        @RequestBody FarmVisitRequest request,
                        Authentication authentication) {
                try {
                        log.info("Request to schedule farm visit for consultation id: {}", consultationId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt to schedule farm visit");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Fetch consultation
                        Optional<Consultation> consultationOpt = consultationService
                                        .getConsultationById(consultationId);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", consultationId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();

                        // Verify consultant owns this consultation
                        if (!consultation.getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to schedule visit for consultation {}", username,
                                                consultationId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to schedule visit for this consultation"));
                        }

                        Optional<Farmvisit> farmvisit = farmvisitService.scheduleFarmVisit(consultation, request);

                        if (farmvisit.isPresent()) {
                                FarmVisitResponse response = mapToResponse(farmvisit.get());
                                log.info("Farm visit scheduled successfully. Visit ID: {}", farmvisit.get().getId());
                                return ResponseEntity.status(HttpStatus.CREATED)
                                                .body(new ApiResponse<>(HttpStatus.CREATED,
                                                                "Farm visit scheduled successfully", response));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to schedule farm visit"));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid request for scheduling farm visit: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error scheduling farm visit: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error scheduling farm visit: " + e.getMessage()));
                }
        }

        // ==================== Farm Visit Retrieval Endpoints ====================

        /**
         * Get all farm visits for a consultation (with authorization check)
         * GET /api/v1/farmvisits/consultation/{consultationId}
         */
        @GetMapping("/consultation/{consultationId}")
        public ResponseEntity<?> getFarmVisitsByConsultationId(
                        @PathVariable Long consultationId,
                        Authentication authentication) {
                try {
                        log.info("Request to get farm visits for consultation id: {}", consultationId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // This method already checks authorization internally
                        Optional<List<Farmvisit>> farmvisitsOpt = farmvisitService.getFarmvisitByConsultationId(
                                        username,
                                        consultationId);

                        if (farmvisitsOpt.isPresent() && !farmvisitsOpt.get().isEmpty()) {
                                List<FarmVisitResponse> responses = farmvisitsOpt.get().stream()
                                                .map(this::mapToResponse)
                                                .collect(Collectors.toList());
                                log.info("Found {} farm visits for consultation {}", responses.size(), consultationId);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Farm visits retrieved successfully", responses));
                        }

                        log.warn("No farm visits found for consultation {}", consultationId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                        "No farm visits found for this consultation"));

                } catch (Exception e) {
                        log.error("Error fetching farm visits: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching farm visits: " + e.getMessage()));
                }
        }

        /**
         * Get all farm visits for a consultation (without authorization - for internal
         * use)
         * GET /api/v1/farmvisits/consultation/{consultationId}/all
         */
        @GetMapping("/consultation/{consultationId}/all")
        public ResponseEntity<?> getAllVisitsForConsultation(
                        @PathVariable Long consultationId,
                        Authentication authentication) {
                try {
                        log.info("Request to get all farm visits for consultation id: {}", consultationId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify user has access to this consultation
                        Optional<Consultation> consultationOpt = consultationService
                                        .getConsultationById(consultationId);
                        if (consultationOpt.isEmpty()) {
                                log.warn("Consultation not found with ID: {}", consultationId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND,
                                                                "Consultation not found"));
                        }

                        Consultation consultation = consultationOpt.get();
                        boolean isAuthorized = consultation.getFarmer().getEmail().equals(username)
                                        || consultation.getConsultant().getEmail().equals(username);

                        if (!isAuthorized) {
                                log.warn("User {} not authorized to view farm visits for consultation {}", username,
                                                consultationId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view these farm visits"));
                        }

                        List<Farmvisit> farmvisits = farmvisitService.getAllVisitsForConsultation(consultationId);
                        List<FarmVisitResponse> responses = farmvisits.stream()
                                        .map(this::mapToResponse)
                                        .collect(Collectors.toList());

                        log.info("Found {} farm visits for consultation {}", responses.size(), consultationId);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Farm visits retrieved successfully",
                                                        responses));

                } catch (Exception e) {
                        log.error("Error fetching farm visits: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching farm visits: " + e.getMessage()));
                }
        }

        /**
         * Get a specific farm visit by ID
         * GET /api/v1/farmvisits/{visitId}
         */
        @GetMapping("/{visitId}")
        public ResponseEntity<?> getFarmVisitById(
                        @PathVariable Long visitId,
                        Authentication authentication) {
                try {
                        log.info("Request to get farm visit with ID: {}", visitId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        Optional<Farmvisit> farmvisitOpt = farmvisitService.getFarmvisitById(visitId);
                        if (farmvisitOpt.isEmpty()) {
                                log.warn("Farm visit not found with ID: {}", visitId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farm visit not found"));
                        }

                        Farmvisit farmvisit = farmvisitOpt.get();

                        // Verify user is authorized (either farmer or consultant)
                        boolean isAuthorized = farmvisit.getConsultation().getFarmer().getEmail().equals(username)
                                        || farmvisit.getConsultation().getConsultant().getEmail().equals(username);

                        if (!isAuthorized) {
                                log.warn("User {} not authorized to view farm visit {}", username, visitId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "You are not authorized to view this farm visit"));
                        }

                        FarmVisitResponse response = mapToResponse(farmvisit);
                        log.info("Farm visit found with ID: {}", visitId);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Farm visit retrieved successfully",
                                                        response));

                } catch (Exception e) {
                        log.error("Error fetching farm visit: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching farm visit: " + e.getMessage()));
                }
        }

        // ==================== Farm Visit Update Endpoints ====================

        /**
         * Update farm visit status
         * PATCH /api/v1/farmvisits/{visitId}/status?status=COMPLETED
         */
        @PatchMapping("/{visitId}/status")
        public ResponseEntity<?> updateVisitStatus(
                        @PathVariable Long visitId,
                        @RequestParam VisitStatus status,
                        Authentication authentication) {
                try {
                        log.info("Request to update farm visit status for visit id: {} to {}", visitId, status);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify visit exists and user is authorized
                        Optional<Farmvisit> farmvisitOpt = farmvisitService.getFarmvisitById(visitId);
                        if (farmvisitOpt.isEmpty()) {
                                log.warn("Farm visit not found with ID: {}", visitId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farm visit not found"));
                        }

                        // Only consultant can update visit status
                        if (!farmvisitOpt.get().getConsultation().getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to update farm visit {}", username, visitId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can update visit status"));
                        }

                        Optional<Farmvisit> updated = farmvisitService.updateVisitStatus(visitId, status);
                        if (updated.isPresent()) {
                                FarmVisitResponse response = mapToResponse(updated.get());
                                log.info("Farm visit status updated successfully for visit {}", visitId);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Visit status updated successfully", response));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to update visit status"));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid status transition: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error updating farm visit status: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error updating visit status: " + e.getMessage()));
                }
        }

        /**
         * Update farm visit notes
         * PATCH /api/v1/farmvisits/{visitId}/notes
         */
        @PatchMapping("/{visitId}/notes")
        public ResponseEntity<?> updateVisitNotes(
                        @PathVariable Long visitId,
                        @RequestBody String notes,
                        Authentication authentication) {
                try {
                        log.info("Request to update farm visit notes for visit id: {}", visitId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify visit exists and user is authorized
                        Optional<Farmvisit> farmvisitOpt = farmvisitService.getFarmvisitById(visitId);
                        if (farmvisitOpt.isEmpty()) {
                                log.warn("Farm visit not found with ID: {}", visitId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farm visit not found"));
                        }

                        // Only consultant can update visit notes
                        if (!farmvisitOpt.get().getConsultation().getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to update farm visit {}", username, visitId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can update visit notes"));
                        }

                        Optional<Farmvisit> updated = farmvisitService.updateVisitNotes(visitId, notes);
                        if (updated.isPresent()) {
                                FarmVisitResponse response = mapToResponse(updated.get());
                                log.info("Farm visit notes updated successfully for visit {}", visitId);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK,
                                                                "Visit notes updated successfully", response));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to update visit notes"));

                } catch (Exception e) {
                        log.error("Error updating farm visit notes: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error updating visit notes: " + e.getMessage()));
                }
        }

        /**
         * Reschedule a farm visit
         * PATCH /api/v1/farmvisits/{visitId}/reschedule
         */
        @PatchMapping("/{visitId}/reschedule")
        public ResponseEntity<?> rescheduleFarmVisit(
                        @PathVariable Long visitId,
                        @RequestBody FarmVisitRequest request,
                        Authentication authentication) {
                try {
                        log.info("Request to reschedule farm visit for visit id: {}", visitId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify visit exists and user is authorized
                        Optional<Farmvisit> farmvisitOpt = farmvisitService.getFarmvisitById(visitId);
                        if (farmvisitOpt.isEmpty()) {
                                log.warn("Farm visit not found with ID: {}", visitId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farm visit not found"));
                        }

                        // Only consultant can reschedule
                        if (!farmvisitOpt.get().getConsultation().getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to reschedule farm visit {}", username, visitId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can reschedule this visit"));
                        }

                        Optional<Farmvisit> rescheduled = farmvisitService.rescheduleFarmVisit(visitId, request);
                        if (rescheduled.isPresent()) {
                                FarmVisitResponse response = mapToResponse(rescheduled.get());
                                log.info("Farm visit rescheduled successfully for visit {}", visitId);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK, "Visit rescheduled successfully",
                                                                response));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to reschedule visit"));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid reschedule request: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error rescheduling farm visit: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error rescheduling visit: " + e.getMessage()));
                }
        }

        // ==================== Farm Visit Action Endpoints ====================

        /**
         * Cancel a farm visit
         * PUT /api/v1/farmvisits/{visitId}/cancel
         */
        @PutMapping("/{visitId}/cancel")
        public ResponseEntity<?> cancelFarmVisit(
                        @PathVariable Long visitId,
                        Authentication authentication) {
                try {
                        log.info("Request to cancel farm visit with ID: {}", visitId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify visit exists and user is authorized
                        Optional<Farmvisit> farmvisitOpt = farmvisitService.getFarmvisitById(visitId);
                        if (farmvisitOpt.isEmpty()) {
                                log.warn("Farm visit not found with ID: {}", visitId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farm visit not found"));
                        }

                        // Only consultant can cancel
                        if (!farmvisitOpt.get().getConsultation().getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to cancel farm visit {}", username, visitId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can cancel this visit"));
                        }

                        Optional<Farmvisit> cancelled = farmvisitService.cancelFarmVisit(visitId);
                        if (cancelled.isPresent()) {
                                FarmVisitResponse response = mapToResponse(cancelled.get());
                                log.info("Farm visit cancelled successfully: {}", visitId);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK, "Visit cancelled successfully",
                                                                response));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to cancel visit"));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid cancellation request: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error cancelling farm visit: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error cancelling visit: " + e.getMessage()));
                }
        }

        /**
         * Complete a farm visit
         * PUT /api/v1/farmvisits/{visitId}/complete
         */
        @PutMapping("/{visitId}/complete")
        public ResponseEntity<?> completeFarmVisit(
                        @PathVariable Long visitId,
                        Authentication authentication) {
                try {
                        log.info("Request to complete farm visit with ID: {}", visitId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify visit exists and user is authorized
                        Optional<Farmvisit> farmvisitOpt = farmvisitService.getFarmvisitById(visitId);
                        if (farmvisitOpt.isEmpty()) {
                                log.warn("Farm visit not found with ID: {}", visitId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farm visit not found"));
                        }

                        // Only consultant can complete
                        if (!farmvisitOpt.get().getConsultation().getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to complete farm visit {}", username, visitId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can complete this visit"));
                        }

                        Optional<Farmvisit> completed = farmvisitService.completeFarmVisit(visitId);
                        if (completed.isPresent()) {
                                FarmVisitResponse response = mapToResponse(completed.get());
                                log.info("Farm visit completed successfully: {}", visitId);
                                return ResponseEntity.ok()
                                                .body(new ApiResponse<>(HttpStatus.OK, "Visit completed successfully",
                                                                response));
                        }

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Failed to complete visit"));

                } catch (IllegalArgumentException e) {
                        log.error("Invalid completion request: {}", e.getMessage());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, e.getMessage()));
                } catch (Exception e) {
                        log.error("Error completing farm visit: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error completing visit: " + e.getMessage()));
                }
        }

        /**
         * Delete a farm visit
         * DELETE /api/v1/farmvisits/{visitId}
         */
        @DeleteMapping("/{visitId}")
        public ResponseEntity<?> deleteFarmVisit(
                        @PathVariable Long visitId,
                        Authentication authentication) {
                try {
                        log.info("Request to delete farm visit with ID: {}", visitId);

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String username = authentication.getName();

                        // Verify visit exists and user is authorized
                        Optional<Farmvisit> farmvisitOpt = farmvisitService.getFarmvisitById(visitId);
                        if (farmvisitOpt.isEmpty()) {
                                log.warn("Farm visit not found with ID: {}", visitId);
                                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                                .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farm visit not found"));
                        }

                        // Only consultant can delete
                        if (!farmvisitOpt.get().getConsultation().getConsultant().getEmail().equals(username)) {
                                log.warn("User {} not authorized to delete farm visit {}", username, visitId);
                                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                                .body(new ApiResponse<>(HttpStatus.FORBIDDEN,
                                                                "Only the assigned consultant can delete this visit"));
                        }

                        farmvisitService.deleteFarmVisit(visitId);
                        log.info("Farm visit deleted successfully: {}", visitId);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Visit deleted successfully", null));

                } catch (Exception e) {
                        log.error("Error deleting farm visit: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error deleting visit: " + e.getMessage()));
                }
        }

        /**
         * Get all farm visits for the logged-in consultant
         * GET /api/v1/farmvisits/consultant/all
         */
        @GetMapping("/consultant/all")
        public ResponseEntity<?> getAllVisitsForConsultant(Authentication authentication) {
                try {
                        log.info("Request to get all farm visits for logged-in consultant");

                        if (authentication == null || !authentication.isAuthenticated()) {
                                log.warn("Unauthorized access attempt");
                                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
                        }

                        String consultantEmail = authentication.getName();
                        log.info("Fetching all farm visits for consultant: {}", consultantEmail);

                        List<Farmvisit> farmvisits = farmvisitService.getAllVisitsForConsultant(consultantEmail);
                        List<FarmVisitResponse> responses = farmvisits.stream()
                                        .map(this::mapToResponse)
                                        .collect(Collectors.toList());

                        log.info("Found {} farm visits for consultant: {}", responses.size(), consultantEmail);
                        return ResponseEntity.ok()
                                        .body(new ApiResponse<>(HttpStatus.OK, "Farm visits retrieved successfully",
                                                        responses));

                } catch (Exception e) {
                        log.error("Error fetching farm visits for consultant: {}", e.getMessage(), e);
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,
                                                        "Error fetching farm visits: " + e.getMessage()));
                }
        }

        // ==================== Helper Methods ====================

        /**
         * Map Farmvisit entity to FarmVisitResponse DTO
         */
        private FarmVisitResponse mapToResponse(Farmvisit farmvisit) {
                AddressDTO addressDTO = null;
                if (farmvisit.getFarmAddress() != null) {
                        Address address = farmvisit.getFarmAddress();
                        addressDTO = new AddressDTO(
                                        address.getStreet(),
                                        address.getCity(),
                                        address.getState(),
                                        address.getPinCode(),
                                        address.getCountry(),
                                        address.getLatitude(),
                                        address.getLongitude());
                }

                return FarmVisitResponse.builder()
                                .id(farmvisit.getId())
                                .consultationId(farmvisit.getConsultation() != null
                                                ? farmvisit.getConsultation().getId()
                                                : null)
                                .consultationTopic(farmvisit.getConsultation() != null
                                                ? farmvisit.getConsultation().getTopic()
                                                : null)
                                .scheduledDate(farmvisit.getScheduledDate())
                                .visitNotes(farmvisit.getVisitNotes())
                                .visitStatus(farmvisit.getVisitStatus())
                                .farmAddress(addressDTO)
                                .createdAt(farmvisit.getCreatedAt())
                                .updatedAt(farmvisit.getUpdatedAt())
                                .isOverdue(farmvisitService.isVisitOverdue(farmvisit))
                                .build();
        }
}