package com.server.service;

import com.server.dto.ConsultationRequestDTO;
import com.server.entity.*;
import com.server.enumeration.ConsultationRequestStatus;
import com.server.repository.ConsultationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ConsultationService {
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private AddressService addressService;

    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private CropService cropService;
    @Autowired
    private SimpleChatService chatService;

    @Transactional
    public Optional<Consultation> createConsultationRequest(
            Farmer farmer,
            ConsultationRequestDTO request) {
        log.info("Creating consultation request for farmer: {}", farmer.getEmail());

        // 1. Find consultant using email (NOT ID)
        Consultant consultant = consultantService.getConsultantByUsername(request.getConsultantEmail())
                .orElseThrow(() -> new RuntimeException(
                        "Consultant not found with username: " + request.getConsultantEmail()));

        log.info("Found consultant: {}", consultant.getEmail());

        // 2. Save or get crop
        Crop crop = new Crop();
        crop.setName(request.getCrop().getName());
        crop.setCategory(request.getCrop().getCategory());
        crop.setType(request.getCrop().getType());
        crop.setDescription(request.getCrop().getDescription());
        crop.setCreatedAt(LocalDateTime.now());
        crop = cropService.save(crop)
                .orElseThrow(() -> new RuntimeException("Failed to save or fetch crop"));
        /*
        
         */
        log.info("Crop linked to consultation: {}", crop.getName());

        // 3. Create Address (Farm Address)
        Address address;
        if (!request.getUseExistingAddress()) {
            log.info("Creating new farm address for consultation");
            address = new Address();
            address.setStreet(request.getFarmAddress().getStreet());
            address.setCity(request.getFarmAddress().getCity());
            address.setState(request.getFarmAddress().getState());
            address.setPinCode(request.getFarmAddress().getPinCode());
            address.setCountry(request.getFarmAddress().getCountry());
            address.setLatitude(request.getFarmAddress().getLatitude());
            address.setLongitude(request.getFarmAddress().getLongitude());
        } else {
            log.info("Using existing farm address for consultation");
            address = farmer.getAddress();
        }
        // Persist address manually (otherwise it will not save)
        Address savedAddress = addressService.save(address)
                .orElseThrow(() -> new RuntimeException("Failed to save or fetch Address"));
        log.info("Saved farm address: {}", savedAddress.getId());

        // 4. Build Consultation Entity
        Consultation consultation = new Consultation();
        consultation.setFarmer(farmer);
        consultation.setConsultant(consultant);
        consultation.setCrop(crop);
        consultation.setFarmAddress(savedAddress);
        consultation.setTopic(request.getTopic());
        consultation.setDescription(request.getDescription());
        consultation.setConsultationRequestStatus(ConsultationRequestStatus.PENDING);
        consultation.setCreatedAt(LocalDateTime.now());
        consultation.setUpdatedAt(LocalDateTime.now());

        Consultation savedConsultation = consultationRepository.save(consultation);
        log.info("Consultation request created with id: {}", savedConsultation.getId());

        return Optional.of(savedConsultation);
    }

    // ==================== Retrieval Methods ====================

    public Optional<Consultation> getConsultationById(Long id) {
        log.info("Fetching consultation request with id: {}", id);
        return consultationRepository.findById(id);
    }

    public List<Consultation> getConsultationsByFarmer(Farmer farmer) {
        log.info("Fetching consultations for farmer: {}", farmer.getEmail());
        return consultationRepository.findByFarmerId(farmer.getId());
    }

    public List<Consultation> getConsultationsByFarmerId(Long farmerId) {
        log.info("Fetching consultations for farmer id: {}", farmerId);
        return consultationRepository.findByFarmerId(farmerId);
    }

    public List<Consultation> getConsultationsByConsultantId(Long consultantId) {
        log.info("Fetching consultations for consultant id: {}", consultantId);
        return consultationRepository.findByConsultantId(consultantId);
    }

    public List<Consultation> getConsultationsByCropId(Long cropId) {
        log.info("Fetching consultations for crop id: {}", cropId);
        return consultationRepository.findByCropId(cropId);
    }

    // ==================== Status-based Retrieval ====================

    public List<Consultation> getConsultationsByStatus(ConsultationRequestStatus status) {
        log.info("Fetching consultations with status: {}", status);
        return consultationRepository.findByConsultationRequestStatus(status);
    }

    public List<Consultation> getConsultationsByFarmerAndStatus(Long farmerId, ConsultationRequestStatus status) {
        log.info("Fetching consultations for farmer {} with status: {}", farmerId, status);
        return consultationRepository.findByFarmerIdAndConsultationRequestStatus(farmerId, status);
    }

    public List<Consultation> getConsultationsByConsultantAndStatus(Long consultantId,
            ConsultationRequestStatus status) {
        log.info("Fetching consultations for consultant {} with status: {}", consultantId, status);
        return consultationRepository.findByConsultantIdAndConsultationRequestStatus(consultantId, status);
    }

    public List<Consultation> getConsultationsByStatusIn(List<ConsultationRequestStatus> statuses) {
        log.info("Fetching consultations with statuses: {}", statuses);
        return consultationRepository.findByStatusIn(statuses);
    }

    public List<Consultation> getConsultationsByFarmerAndStatusIn(Long farmerId,
            List<ConsultationRequestStatus> statuses) {
        log.info("Fetching consultations for farmer {} with statuses: {}", farmerId, statuses);
        return consultationRepository.findByFarmerIdAndStatusIn(farmerId, statuses);
    }

    public List<Consultation> getConsultationsByConsultantAndStatusIn(Long consultantId,
            List<ConsultationRequestStatus> statuses) {
        log.info("Fetching consultations for consultant {} with statuses: {}", consultantId, statuses);
        return consultationRepository.findByConsultantIdAndStatusIn(consultantId, statuses);
    }

    // ==================== Date-based Retrieval ====================

    public List<Consultation> getRecentConsultations() {
        log.info("Fetching top 10 recent consultations");
        return consultationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public List<Consultation> getConsultationsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching consultations between {} and {}", startDate, endDate);
        return consultationRepository.findByCreatedAtBetween(startDate, endDate);
    }

    public List<Consultation> getConsultationsAfterDate(LocalDateTime date) {
        log.info("Fetching consultations created after {}", date);
        return consultationRepository.findByCreatedAtAfter(date);
    }

    public List<Consultation> getConsultationsByStatusAndDateAfter(ConsultationRequestStatus status,
            LocalDateTime date) {
        log.info("Fetching consultations with status {} created after {}", status, date);
        return consultationRepository.findByConsultationRequestStatusAndCreatedAtAfter(status, date);
    }

    // ==================== Status Management Methods ====================

    @Transactional
    public Optional<Consultation> approveConsultation(Long consultationId) {
        log.info("Approving consultation with id: {}", consultationId);

        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (consultationOpt.isEmpty()) {
            log.error("Consultation not found with id: {}", consultationId);
            return Optional.empty();
        }

        Consultation consultation = consultationOpt.get();

        if (consultation.getConsultationRequestStatus() != ConsultationRequestStatus.PENDING) {
            log.warn("Cannot approve consultation {} - current status is {}",
                    consultationId, consultation.getConsultationRequestStatus());
            throw new RuntimeException("Only PENDING consultations can be approved");
        }

        consultation.setConsultationRequestStatus(ConsultationRequestStatus.APPROVED);
        consultation.setUpdatedAt(LocalDateTime.now());

        Consultation updated = consultationRepository.save(consultation);
        log.info("Consultation {} approved successfully", consultationId);

        // Auto-create chat room using SimpleChatService
        try {
            chatService.createChatRoom(updated);
            log.info("Chat room created for consultation: {}", consultationId);
        } catch (Exception e) {
            log.error("Failed to create chat room for consultation {}: {}", consultationId, e.getMessage());
            // Don't fail the transaction if chat creation fails, just log it
        }

        return Optional.of(updated);
    }

    @Transactional
    public Optional<Consultation> rejectConsultation(Long consultationId) {
        log.info("Rejecting consultation with id: {}", consultationId);

        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (consultationOpt.isEmpty()) {
            log.error("Consultation not found with id: {}", consultationId);
            return Optional.empty();
        }

        Consultation consultation = consultationOpt.get();

        if (consultation.getConsultationRequestStatus() != ConsultationRequestStatus.PENDING) {
            log.warn("Cannot reject consultation {} - current status is {}",
                    consultationId, consultation.getConsultationRequestStatus());
            throw new RuntimeException("Only PENDING consultations can be rejected");
        }

        consultation.setConsultationRequestStatus(ConsultationRequestStatus.REJECTED);
        consultation.setUpdatedAt(LocalDateTime.now());

        Consultation updated = consultationRepository.save(consultation);
        log.info("Consultation {} rejected successfully", consultationId);

        return Optional.of(updated);
    }

    @Transactional
    public Optional<Consultation> completeConsultation(Long consultationId) {
        log.info("Completing consultation with id: {}", consultationId);

        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (consultationOpt.isEmpty()) {
            log.error("Consultation not found with id: {}", consultationId);
            return Optional.empty();
        }

        Consultation consultation = consultationOpt.get();

        if (consultation.getConsultationRequestStatus() != ConsultationRequestStatus.APPROVED) {
            log.warn("Cannot complete consultation {} - current status is {}",
                    consultationId, consultation.getConsultationRequestStatus());
            throw new RuntimeException("Only APPROVED consultations can be completed");
        }

        consultation.setConsultationRequestStatus(ConsultationRequestStatus.COMPLETED);
        consultation.setUpdatedAt(LocalDateTime.now());
        consultation.setClosedAt(LocalDateTime.now());

        Consultation updated = consultationRepository.save(consultation);
        log.info("Consultation {} completed successfully", consultationId);

        return Optional.of(updated);
    }

    @Transactional
    public Optional<Consultation> updateConsultationStatus(Long consultationId, ConsultationRequestStatus newStatus) {
        log.info("Updating consultation {} status to {}", consultationId, newStatus);

        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (consultationOpt.isEmpty()) {
            log.error("Consultation not found with id: {}", consultationId);
            return Optional.empty();
        }

        Consultation consultation = consultationOpt.get();
        consultation.setConsultationRequestStatus(newStatus);
        consultation.setUpdatedAt(LocalDateTime.now());

        if (newStatus == ConsultationRequestStatus.COMPLETED) {
            consultation.setClosedAt(LocalDateTime.now());
        }

        Consultation updated = consultationRepository.save(consultation);
        log.info("Consultation {} status updated to {}", consultationId, newStatus);

        return Optional.of(updated);
    }

    // ==================== Statistics Methods ====================

    public Long getConsultationCountByFarmer(Long farmerId) {
        log.info("Counting consultations for farmer: {}", farmerId);
        return consultationRepository.countByFarmerId(farmerId);
    }

    public Long getConsultationCountByConsultant(Long consultantId) {
        log.info("Counting consultations for consultant: {}", consultantId);
        return consultationRepository.countByConsultantId(consultantId);
    }

    public Long getConsultationCountByStatus(ConsultationRequestStatus status) {
        log.info("Counting consultations with status: {}", status);
        return consultationRepository.countByConsultationRequestStatus(status);
    }

    public Long getConsultationCountByConsultantAndStatus(Long consultantId, ConsultationRequestStatus status) {
        log.info("Counting consultations for consultant {} with status: {}", consultantId, status);
        return consultationRepository.countByConsultantIdAndConsultationRequestStatus(consultantId, status);
    }

    public Long getConsultationCountByFarmerAndStatus(Long farmerId, ConsultationRequestStatus status) {
        log.info("Counting consultations for farmer {} with status: {}", farmerId, status);
        Long count = (long) consultationRepository.findByFarmerIdAndConsultationRequestStatus(farmerId, status).size();
        return count;
    }

    public Long getConsultationCountAfterDate(LocalDateTime date) {
        log.info("Counting consultations created after: {}", date);
        return consultationRepository.countByCreatedAtAfter(date);
    }

    public Long getConsultationCountByCrop(Long cropId) {
        log.info("Counting consultations for crop: {}", cropId);
        return consultationRepository.countByCropId(cropId);
    }

    // ==================== Validation Methods ====================

    public boolean hasActiveConsultation(Long farmerId, Long consultantId) {
        log.info("Checking for active consultation between farmer {} and consultant {}", farmerId, consultantId);
        return consultationRepository.existsByFarmerIdAndConsultantIdAndConsultationRequestStatus(
                farmerId, consultantId, ConsultationRequestStatus.APPROVED);
    }

    public boolean canCreateConsultation(Long farmerId, Long consultantId) {
        log.info("Validating if farmer {} can create consultation with consultant {}", farmerId, consultantId);
        // Check if there's already a pending consultation
        boolean hasPending = consultationRepository.existsByFarmerIdAndConsultantIdAndConsultationRequestStatus(
                farmerId, consultantId, ConsultationRequestStatus.PENDING);

        if (hasPending) {
            log.warn("Farmer {} already has a pending consultation with consultant {}", farmerId, consultantId);
            return false;
        }

        return true;
    }

    // ==================== Farm Visit Management ====================

    @Transactional
    public Optional<Consultation> addFarmVisitToConsultation(Consultation consultation, Farmvisit farmvisit) {
        log.info("Adding farm visit to consultation with id: {}", consultation.getId());
        consultation.getFarmVisits().add(farmvisit);
        Consultation updatedConsultation = consultationRepository.save(consultation);
        log.info("Farm visit added to consultation with id: {}", consultation.getId());
        return Optional.of(updatedConsultation);
    }

    @Transactional
    public Optional<Consultation> addFarmVisitById(Long consultationId, Farmvisit farmvisit) {
        log.info("Adding farm visit to consultation id: {}", consultationId);

        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (consultationOpt.isEmpty()) {
            log.error("Consultation not found with id: {}", consultationId);
            return Optional.empty();
        }

        Consultation consultation = consultationOpt.get();
        consultation.getFarmVisits().add(farmvisit);
        Consultation updated = consultationRepository.save(consultation);
        log.info("Farm visit added to consultation {}", consultationId);

        return Optional.of(updated);
    }

    // ==================== Update Methods ====================

    @Transactional
    public Optional<Consultation> updateConsultation(Long consultationId, String topic, String description) {
        log.info("Updating consultation {}", consultationId);

        Optional<Consultation> consultationOpt = consultationRepository.findById(consultationId);
        if (consultationOpt.isEmpty()) {
            log.error("Consultation not found with id: {}", consultationId);
            return Optional.empty();
        }

        Consultation consultation = consultationOpt.get();

        if (topic != null && !topic.isEmpty()) {
            consultation.setTopic(topic);
        }
        if (description != null && !description.isEmpty()) {
            consultation.setDescription(description);
        }
        consultation.setUpdatedAt(LocalDateTime.now());

        Consultation updated = consultationRepository.save(consultation);
        log.info("Consultation {} updated successfully", consultationId);

        return Optional.of(updated);
    }

    // ==================== Delete Methods ====================

    @Transactional
    public boolean deleteConsultation(Long consultationId) {
        log.info("Deleting consultation {}", consultationId);

        if (!consultationRepository.existsById(consultationId)) {
            log.error("Consultation not found with id: {}", consultationId);
            return false;
        }

        consultationRepository.deleteById(consultationId);
        log.info("Consultation {} deleted successfully", consultationId);
        return true;
    }
}
