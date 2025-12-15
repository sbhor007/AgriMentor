
package com.server.service;

import com.server.dto.FarmVisitRequest;
import com.server.entity.Consultation;
import com.server.entity.Farmvisit;
import com.server.enumeration.VisitStatus;
import com.server.repository.FarmvisitRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class FarmvisitService {
    @Autowired
    private FarmvisitRepository farmvisitRepository;

    /**
     * Save a farm visit
     */
    public Optional<Farmvisit> save(Farmvisit farmvisit) {
        log.debug("Saving farm visit: {}", farmvisit.getId());
        try {
            return Optional.of(farmvisitRepository.save(farmvisit));
        } catch (Exception e) {
            log.error("Error saving farm visit", e);
            throw new RuntimeException("Failed to save farm visit: " + e.getMessage());
        }
    }

    /**
     * Schedule a new farm visit for a consultation
     */
    @Transactional
    public Optional<Farmvisit> scheduleFarmVisit(Consultation consultation, FarmVisitRequest request) {
        log.info("Scheduling farm visit for consultation id: {}", consultation.getId());
        try {
            // Validate consultation
            if (consultation == null || consultation.getId() == null) {
                throw new IllegalArgumentException("Invalid consultation provided");
            }

            // Validate request data
            if (request.getScheduledDate() == null) {
                throw new IllegalArgumentException("Scheduled date cannot be null");
            }

            // Validate scheduled date is in future
            if (request.getScheduledDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Scheduled date must be in the future");
            }

            Farmvisit farmvisit = new Farmvisit();
            farmvisit.setConsultation(consultation);
            farmvisit.setScheduledDate(request.getScheduledDate());
            farmvisit.setVisitNotes(request.getVisitNotes() != null ? request.getVisitNotes() : "");
            farmvisit.setVisitStatus(
                    request.getVisitStatus() != null ? request.getVisitStatus() : VisitStatus.SCHEDULED);
            farmvisit.setFarmAddress(consultation.getFarmAddress());

            log.debug("Created farm visit entity with scheduled date: {}", request.getScheduledDate());
            farmvisit = this.save(farmvisit).orElseThrow(() -> new RuntimeException("Failed to save farm visit"));
            log.info("Farm visit scheduled successfully. Visit ID: {}, Consultation ID: {}", farmvisit.getId(),
                    consultation.getId());

            return Optional.ofNullable(farmvisit);
        } catch (Exception e) {
            log.error("Error scheduling farm visit for consultation id: {}", consultation.getId(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get a single farm visit by consultation id AND verify consultant owns it
     */
    public Optional<List<Farmvisit>> getFarmvisitByConsultationId(String username, Long consultationId) {
        log.info("Fetching farm visit for consultation id: {} by consultant: {}", consultationId, username);

        try {
            // Fetch visit by consultation + consultant email
            Optional<List<Farmvisit>> farmvisit = farmvisitRepository
                    .findByConsultationIdAndConsultation_Consultant_Email(consultationId, username);

            if (farmvisit.isEmpty()) {
                log.warn("No farm visit found for consultation id {} for consultant {}", consultationId, username);
            } else {
                log.info("Farm visit found: {}", farmvisit.get());
            }

            return farmvisit;

        } catch (Exception e) {
            log.error("Error retrieving farm visit for consultation id: {}", consultationId, e);
            throw new RuntimeException("Error fetching farm visit: " + e.getMessage());
        }
    }

    /**
     * Get all farm visits for a consultation
     */
    public List<Farmvisit> getAllVisitsForConsultation(Long consultationId) {
        log.info("Fetching all farm visits for consultation id: {}", consultationId);
        try {
            return farmvisitRepository.findAllByConsultationId(consultationId);
        } catch (Exception e) {
            log.error("Error fetching all farm visits for consultation id: {}", consultationId, e);
            throw new RuntimeException("Error fetching farm visits: " + e.getMessage());
        }
    }

    /**
     * Get a specific farm visit by id
     */
    public Optional<Farmvisit> getFarmvisitById(Long visitId) {
        log.info("Fetching farm visit with id: {}", visitId);
        try {
            return farmvisitRepository.findById(visitId);
        } catch (Exception e) {
            log.error("Error fetching farm visit with id: {}", visitId, e);
            throw new RuntimeException("Error fetching farm visit: " + e.getMessage());
        }
    }

    /**
     * Update farm visit status
     */
    @Transactional
    public Optional<Farmvisit> updateVisitStatus(Long visitId, VisitStatus newStatus) {
        log.info("Updating farm visit status. Visit ID: {}, New Status: {}", visitId, newStatus);
        try {
            Optional<Farmvisit> farmvisitOpt = this.getFarmvisitById(visitId);
            if (farmvisitOpt.isEmpty()) {
                throw new RuntimeException("Farm visit with id " + visitId + " not found");
            }

            Farmvisit farmvisit = farmvisitOpt.get();
            VisitStatus oldStatus = farmvisit.getVisitStatus();

            // Validate status transition
            if (!isValidStatusTransition(oldStatus, newStatus)) {
                throw new IllegalArgumentException("Invalid status transition from " + oldStatus + " to " + newStatus);
            }

            farmvisit.setVisitStatus(newStatus);
            log.debug("Status updated from {} to {}", oldStatus, newStatus);

            return this.save(farmvisit);
        } catch (Exception e) {
            log.error("Error updating farm visit status for visit id: {}", visitId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Update farm visit notes
     */
    @Transactional
    public Optional<Farmvisit> updateVisitNotes(Long visitId, String notes) {
        log.info("Updating farm visit notes for visit id: {}", visitId);
        try {
            Optional<Farmvisit> farmvisitOpt = this.getFarmvisitById(visitId);
            if (farmvisitOpt.isEmpty()) {
                throw new RuntimeException("Farm visit with id " + visitId + " not found");
            }

            Farmvisit farmvisit = farmvisitOpt.get();
            farmvisit.setVisitNotes(notes != null ? notes : "");
            log.debug("Notes updated for visit id: {}", visitId);

            return this.save(farmvisit);
        } catch (Exception e) {
            log.error("Error updating farm visit notes for visit id: {}", visitId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Reschedule a farm visit
     */
    @Transactional
    public Optional<Farmvisit> rescheduleFarmVisit(Long visitId, FarmVisitRequest request) {
        log.info("Rescheduling farm visit with id: {}", visitId);
        try {
            Optional<Farmvisit> farmvisitOpt = this.getFarmvisitById(visitId);
            if (farmvisitOpt.isEmpty()) {
                throw new RuntimeException("Farm visit with id " + visitId + " not found");
            }

            // Validate new scheduled date
            if (request.getScheduledDate() == null) {
                throw new IllegalArgumentException("New scheduled date cannot be null");
            }

            if (request.getScheduledDate().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Scheduled date must be in the future");
            }

            Farmvisit farmvisit = farmvisitOpt.get();
            LocalDateTime oldDate = farmvisit.getScheduledDate();

            farmvisit.setScheduledDate(request.getScheduledDate());

            // Update notes if provided
            if (request.getVisitNotes() != null && !request.getVisitNotes().isEmpty()) {
                farmvisit.setVisitNotes(request.getVisitNotes());
            }

            // Reset status to SCHEDULED if rescheduling
            farmvisit.setVisitStatus(VisitStatus.SCHEDULED);

            log.debug("Visit rescheduled from {} to {}", oldDate, request.getScheduledDate());

            return this.save(farmvisit);
        } catch (Exception e) {
            log.error("Error rescheduling farm visit with id: {}", visitId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Cancel a farm visit
     */
    @Transactional
    public Optional<Farmvisit> cancelFarmVisit(Long visitId) {
        log.info("Cancelling farm visit with id: {}", visitId);
        try {
            return this.updateVisitStatus(visitId, VisitStatus.CANCELLED);
        } catch (Exception e) {
            log.error("Error cancelling farm visit with id: {}", visitId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Complete a farm visit
     */
    @Transactional
    public Optional<Farmvisit> completeFarmVisit(Long visitId) {
        log.info("Completing farm visit with id: {}", visitId);
        try {
            return this.updateVisitStatus(visitId, VisitStatus.COMPLETED);
        } catch (Exception e) {
            log.error("Error completing farm visit with id: {}", visitId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Delete a farm visit
     */
    @Transactional
    public void deleteFarmVisit(Long visitId) {
        log.info("Deleting farm visit with id: {}", visitId);
        try {
            Optional<Farmvisit> farmvisit = this.getFarmvisitById(visitId);
            if (farmvisit.isEmpty()) {
                throw new RuntimeException("Farm visit with id " + visitId + " not found");
            }

            farmvisitRepository.deleteById(visitId);
            log.info("Farm visit with id: {} deleted successfully", visitId);
        } catch (Exception e) {
            log.error("Error deleting farm visit with id: {}", visitId, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Validate status transition
     */
    private boolean isValidStatusTransition(VisitStatus currentStatus, VisitStatus newStatus) {
        // Define valid transitions
        return switch (currentStatus) {
            case SCHEDULED -> newStatus == VisitStatus.COMPLETED || newStatus == VisitStatus.CANCELLED
                    || newStatus == VisitStatus.MISSED;
            case COMPLETED -> false; // Cannot transition from completed
            case CANCELLED -> false; // Cannot transition from cancelled
            case MISSED -> newStatus == VisitStatus.SCHEDULED; // Can reschedule a missed visit
        };
    }

    /**
     * Check if a farm visit is overdue
     */
    public boolean isVisitOverdue(Farmvisit farmvisit) {
        log.debug("Checking if visit is overdue. Visit ID: {}", farmvisit.getId());
        return farmvisit.getVisitStatus() == VisitStatus.SCHEDULED &&
                farmvisit.getScheduledDate().isBefore(LocalDateTime.now());
    }

    /**
     * Get all farm visits for a consultant
     */
    public List<Farmvisit> getAllVisitsForConsultant(String consultantEmail) {
        log.info("Fetching all farm visits for consultant: {}", consultantEmail);
        try {
            List<Farmvisit> visits = farmvisitRepository.findByConsultation_Consultant_Email(consultantEmail);
            log.info("Found {} farm visits for consultant: {}", visits.size(), consultantEmail);
            return visits;
        } catch (Exception e) {
            log.error("Error fetching farm visits for consultant: {}", consultantEmail, e);
            throw new RuntimeException("Error fetching farm visits: " + e.getMessage());
        }
    }

}