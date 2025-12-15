package com.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.server.dto.AddressDTO;
import com.server.dto.ConsultationDTO.ConsultationResponse;
import com.server.dto.CropDTO;
import com.server.dto.FarmVisitRequest;
import com.server.dto.consultantDTO.ConsultantResponse;
import com.server.dto.consultantDTO.ConsultantShortDTO;
import com.server.dto.consultantDTO.ConsultantUpdateRequest;
import com.server.dto.verificationDocumentDTOs.VerificationDocumentDTO;
import com.server.entity.*;
import com.server.enumeration.ConsultationRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.dto.ConsultantDTO;
import com.server.enumeration.VerificationStatus;
import com.server.repository.ConsultantRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConsultantService {
    @Autowired
    private ConsultantRepository consultantRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private FarmvisitService farmvisitService;

    // Get all verified consultants
    @Transactional
    public List<ConsultantResponse> getVerifiedConsultants() {
        log.info("Fetching verified consultants from database");
        try {
            List<Consultant> consultants = consultantRepository.findByVerificationStatus(VerificationStatus.VERIFIED);

            if (consultants.isEmpty()) {
                log.warn("No verified consultants found in database");
                return new ArrayList<>();
            }

            List<ConsultantResponse> consultantResponses = consultants.stream()
                    .map(consultant -> {
                        ConsultantResponse response = new ConsultantResponse();
                        response.setId(consultant.getId());
                        response.setFirstName(consultant.getFirstName());
                        response.setLastName(consultant.getLastName());
                        response.setEmail(consultant.getEmail());
                        response.setPhone(consultant.getPhone());
                        response.setExpertiseArea(consultant.getExpertiseArea());
                        response.setExperienceYears(consultant.getExperienceYears());
                        response.setQualifications(consultant.getQualifications());
                        response.setSpecialization(consultant.getSpecialization());
                        response.setVerificationStatus(consultant.getVerificationStatus());
                        response.setIsActive(consultant.getIsActive());



                        if(consultant.getAddress() != null){
                            Address address = consultant.getAddress();
                            AddressDTO addressDTO = new AddressDTO();
                            addressDTO.setStreet(address.getStreet());
                            addressDTO.setCity(address.getCity());
                            addressDTO.setState(address.getState());
                            addressDTO.setCountry(address.getCountry());
                            addressDTO.setPinCode(address.getPinCode());
                            response.setAddress(addressDTO);
                            response.setBio(consultant.getBio());
                        }

                        if(consultant.getVerificationDocument() != null){
                            VerificationDocument doc = consultant.getVerificationDocument();
                            VerificationDocumentDTO docDTO = new VerificationDocumentDTO();
                            docDTO.setId(doc.getId());
                            docDTO.setDocumentType(doc.getDocumentType());
                            docDTO.setDocumentUrl("/api/v1/documents/" + doc.getId() + "/download");
                            response.setVerificationDocument(docDTO);
                        }

                        // Add profile picture URL if exists
                        if (consultant.getProfilePicture() != null && consultant.getProfilePicture().getIsActive()) {
                            response.setProfilePhotoUrl(
                                    "http://localhost:8080/api/v1/consultants/profile-picture/" + consultant.getId());
                        }

                        return response;
                    })
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} verified consultants from database", consultantResponses.size());
            return consultantResponses;
        } catch (Exception e) {
            log.error("Error fetching verified consultants", e);
            throw new RuntimeException("Failed to fetch verified consultants: " + e.getMessage());
        }
    }

    @Transactional
    public List<ConsultantResponse> getAllConsultants() {
        log.info("Fetching all consultants from database");
        try {
            List<Consultant> consultants = consultantRepository.findAll();

            log.info("Successfully fetched {} consultants from database", consultants);

            if (consultants.isEmpty()) {
                log.warn("No consultants found in database");
                return new ArrayList<>();
            }

            List<ConsultantResponse> consultantResponses = consultants.stream()
                    .map(consultant -> {
                        ConsultantResponse response = new ConsultantResponse();
                        response.setId(consultant.getId());
                        response.setFirstName(consultant.getFirstName());
                        response.setLastName(consultant.getLastName());
                        response.setEmail(consultant.getEmail());
                        response.setPhone(consultant.getPhone());
                        response.setExpertiseArea(consultant.getExpertiseArea());
                        response.setExperienceYears(consultant.getExperienceYears());
                        response.setQualifications(consultant.getQualifications());
                        response.setSpecialization(consultant.getSpecialization());
                        response.setVerificationStatus(consultant.getVerificationStatus());
                        response.setIsActive(consultant.getIsActive());
                        log.info("Consultant {}", consultant);
                        if (consultant.getAddress() != null) {
                            AddressDTO addressDTO = new AddressDTO();
                            addressDTO.setCity(consultant.getAddress().getCity());
                            addressDTO.setState(consultant.getAddress().getState());
                            addressDTO.setCountry(consultant.getAddress().getCountry());
                            addressDTO.setPinCode(consultant.getAddress().getPinCode());
                            addressDTO.setStreet(consultant.getAddress().getStreet());
                            addressDTO.setLatitude(consultant.getAddress().getLatitude());
                            addressDTO.setLongitude(consultant.getAddress().getLongitude());
                            response.setAddress(addressDTO);
                        }
                        if (consultant.getVerificationDocument() != null) {
                            VerificationDocument doc = consultant.getVerificationDocument();
                            VerificationDocumentDTO docDTO = new VerificationDocumentDTO();
                            docDTO.setId(doc.getId());
                            docDTO.setDocumentType(doc.getDocumentType());
                            docDTO.setDocumentUrl("/api/v1/documents/" + doc.getId() + "/download");
                            response.setVerificationDocument(docDTO);
                        }

                        // Add profile picture URL if exists
                        if (consultant.getProfilePicture() != null && consultant.getProfilePicture().getIsActive()) {
                            response.setProfilePhotoUrl(
                                    "http://localhost:8080/api/v1/consultants/profile-picture/" + consultant.getId());
                        }

                        return response;
                    })
                    .collect(Collectors.toList());

            log.info("Successfully fetched {} consultants from database", consultantResponses.size());
            return consultantResponses;
        } catch (Exception e) {
            log.error("Error fetching all consultants", e);
            throw new RuntimeException("Failed to fetch consultants: " + e.getMessage());
        }
    }

    // Get consultant by ID
    public Optional<Consultant> getConsultantById(Long id) {
        log.info("Inside ConsultantService.getConsultantById with id: {}", id);
        return consultantRepository.findById(id);
    }

    // Get consultant by email
    public Optional<Consultant> getConsultantByUsername(String username) {
        log.info("Inside ConsultantService.getConsultantByEmail with email: {}", username);
        return consultantRepository.findByEmail(username);
    }

    // Check if consultant is verified
    public boolean isVerified(String email) {
        log.info("Inside ConsultantService.isVerified with email: {}", email);
        Optional<Consultant> consultantOpt = this.getConsultantByUsername(email);
        if (consultantOpt.isPresent()) {
            return consultantOpt.get().getIsVerified();
        } else {
            log.warn("Consultant with email {} not found", email);
            return false;
        }
    }

    // Check verification status
    public VerificationStatus checkVerificationStatus(String email) {
        log.info("Inside ConsultantService.checkVerificationStatus with email: {}", email);
        Optional<Consultant> consultantOpt = this.getConsultantByUsername(email);

        if (consultantOpt.isPresent()) {
            log.info("Consultant with email {} found with verification status: {}", email,
                    consultantOpt.get().getVerificationStatus());
            return consultantOpt.get().getVerificationStatus();
        } else {
            log.warn("Consultant with email {} not found", email);
            return null;
        }
    }

    // update verification status
    @Transactional
    public void updateVerificationStatus(String email, VerificationStatus status) {
        log.info("Inside ConsultantService.updateVerificationStatus with email: {} and status: {}", email, status);
        Optional<Consultant> consultantOpt = this.getConsultantByUsername(email);
        if (consultantOpt.isPresent()) {
            Consultant consultant = consultantOpt.get();
            consultant.setVerificationStatus(status);
            if (status == VerificationStatus.VERIFIED) {
                consultant.setIsVerified(true);
                consultant.setIsActive(true);
            } else {
                consultant.setIsVerified(false);
                consultant.setIsActive(false);
            }
            consultantRepository.save(consultant);
            log.info("Consultant with email {} updated to verification status: {}", email, status);
        } else {
            log.warn("Consultant with email {} not found. Cannot update verification status.", email);
            throw new RuntimeException("Consultant not found");
        }
    }

    // verify consultant
    @Transactional
    public boolean verifyConsultant(String email) {
        log.info("Inside ConsultantService.verifyConsultant with email: {}", email);
        try {
            this.updateVerificationStatus(email, VerificationStatus.VERIFIED);
            log.info("Consultant with email {} verified successfully", email);
            this.emailService.sendEmail(
                    "Dear Consultant,\n\n" +
                            "We are pleased to inform you that your account has been successfully verified. " +
                            "You can now access all the features and services available to verified consultants on our platform.\n\n"
                            +
                            "Thank you for being a valued member of our community.\n\n" +
                            "Best regards,\n" +
                            "The Team");
            return true;
        } catch (Exception e) {
            log.error("Error verifying consultant with email {}: {}", email, e.getMessage());
            return false;
        }
    }

    // Remove Consultant
    @Transactional
    public boolean deleteConsultant(String username) {
        log.info("Inside ConsultantService.deleteConsultant with username: {}", username);
        try {
            Consultant consultant = this.getConsultantByUsername(username).orElse(null);
            log.info("Consultant fetched for deletion: {}", consultant);
            if (consultant != null) {
                consultantRepository.delete(consultant);
                log.info("Consultant with username {} deleted successfully", username);
                return true;
            } else {
                log.warn("Consultant with username {} not found. Cannot delete.", username);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting consultant with username {}: {}", username, e.getMessage());
            return false;
        }
    }

    // gell all consultation requests of consultant
    public Optional<List<ConsultationResponse>> getAllConsultationRequests(String username) {
        log.info("Fetching all consultation requests for username: {}", username);
        try {
            // Find consultant by email/username
            Consultant consultant = consultantRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("consultant not found with username: " + username));

            // Fetch consultations with crop details
            List<ConsultationResponse> responses = consultant.getConsultations().stream()
                    .map(consultation -> {
                        ConsultationResponse dto = new ConsultationResponse();
                        dto.setId(consultation.getId());
                        dto.setTopic(consultation.getTopic());
                        dto.setDescription(consultation.getDescription());
                        dto.setConsultationRequestStatus(consultation.getConsultationRequestStatus());
                        dto.setCreatedAt(consultation.getCreatedAt());
                        dto.setUpdatedAt(consultation.getUpdatedAt());
                        dto.setClosedAt(consultation.getClosedAt());
                        dto.setFarmAddress(consultation.getFarmAddress());
                        dto.setFarmVisits(consultation.getFarmVisits());
                        dto.setConsultationReports(consultation.getConsultationReports());
                        dto.setFarmer(consultation.getFarmer());
                        dto.setConsultant(consultation.getConsultant());

                        // Map crop details
                        if (consultation.getCrop() != null) {
                            CropDTO cropDTO = new CropDTO();
                            Crop crop = consultation.getCrop();
                            cropDTO.setName(crop.getName());
                            cropDTO.setCategory(crop.getCategory());
                            cropDTO.setType(crop.getType());
                            cropDTO.setDescription(crop.getDescription());
                            dto.setCrop(cropDTO);
                        }

                        return dto;
                    })
                    .collect(Collectors.toList());

            log.info("Found {} consultation requests for username: {}", responses.size(), username);
            return Optional.of(responses);

        } catch (Exception e) {
            log.error("Failed to fetch consultation requests for username {}: {}", username, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch consultation requests: " + e.getMessage(), e);
        }
    }

    @Transactional
    public boolean acceptConsultationRequest(String username, Long consultationId) {
        log.info("Consultant {} is attempting to accept consultation request with id: {}", username, consultationId);
        try {
            Consultant consultant = consultantRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Consultant not found with username: " + username));

            Optional<Consultation> consultationOpt = consultant.getConsultations().stream()
                    .filter(c -> c.getId().equals(consultationId))
                    .findFirst();

            if (consultationOpt.isPresent()) {
                Consultation consultation = consultationOpt.get();
                consultation.setConsultationRequestStatus(ConsultationRequestStatus.APPROVED);
                log.info("Consultation request with id: {} accepted by consultant: {}", consultationId, username);
                return true;
            } else {
                log.warn("Consultation request with id: {} not found for consultant: {}", consultationId, username);
                return false;
            }
        } catch (Exception e) {
            log.error("Error accepting consultation request with id: {} for consultant: {}: {}", consultationId,
                    username, e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean rejectConsultationRequest(String username, Long consultationId) {
        log.info("Consultant {} is attempting to accept consultation request with id: {}", username, consultationId);
        try {
            Consultant consultant = consultantRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Consultant not found with username: " + username));

            Optional<Consultation> consultationOpt = consultant.getConsultations().stream()
                    .filter(c -> c.getId().equals(consultationId))
                    .findFirst();

            if (consultationOpt.isPresent()) {
                Consultation consultation = consultationOpt.get();
                consultation.setConsultationRequestStatus(ConsultationRequestStatus.REJECTED);
                log.info("Consultation request with id: {} accepted by consultant: {}", consultationId, username);
                return true;
            } else {
                log.warn("Consultation request with id: {} not found for consultant: {}", consultationId, username);
                return false;
            }
        } catch (Exception e) {
            log.error("Error accepting consultation request with id: {} for consultant: {}: {}", consultationId,
                    username, e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean scheduleConsultatinVisit(String username, Long consultationId, FarmVisitRequest request) {
        log.info("Consultant {} is attempting to schedule farm visit for consultation request with id: {}", username,
                consultationId);
        try {
            Consultant consultant = consultantRepository.findByEmail(username)
                    .orElseThrow(() -> new RuntimeException("Consultant not found with username: " + username));

            Optional<Consultation> consultationOpt = consultant.getConsultations().stream()
                    .filter(c -> c.getId().equals(consultationId))
                    .findFirst();

            if (consultationOpt.isPresent()) {
                Consultation consultation = consultationOpt.get();
                Farmvisit scheduled = farmvisitService.scheduleFarmVisit(consultation, request).orElse(null);
                if (scheduled != null) {
                    log.info("Farm visit scheduled for consultation request with id: {} by consultant: {}",
                            consultationId, username);
                    consultation.getFarmVisits().add(scheduled);
                    log.info("Linked farm visit to consultation successfully for consultation id: {}",
                            consultation.getId());
                    return true;
                } else {
                    log.warn("Failed to schedule farm visit for consultation request with id: {} by consultant: {}",
                            consultationId, username);
                    return false;
                }
            } else {
                log.warn("Consultation request with id: {} not found for consultant: {}", consultationId, username);
                return false;
            }
        } catch (Exception e) {
            log.error("Error scheduling farm visit for consultation request with id: {} for consultant: {}: {}",
                    consultationId, username, e.getMessage());
            return false;
        }
    }

    // Update Consultant Profile

    // Update Consultant Profile
    @Transactional
    public boolean updateConsultantProfile(String username, ConsultantUpdateRequest updateRequest) {
        log.info("Updating consultant profile for user: {}", username);
        try {
            Optional<Consultant> consultantOptional = this.getConsultantByUsername(username);
            if (consultantOptional.isEmpty()) {
                log.warn("Consultant not found for username: {}", username);
                return false;
            }

            Consultant consultant = consultantOptional.get();

            // Update personal and professional fields if provided
            if (updateRequest.getFirstName() != null && !updateRequest.getFirstName().isEmpty()) {
                consultant.setFirstName(updateRequest.getFirstName());
                log.debug("Updated first name for consultant: {}", username);
            }

            if (updateRequest.getLastName() != null && !updateRequest.getLastName().isEmpty()) {
                consultant.setLastName(updateRequest.getLastName());
                log.debug("Updated last name for consultant: {}", username);
            }

            if (updateRequest.getPhone() != null && !updateRequest.getPhone().isEmpty()) {
                consultant.setPhone(updateRequest.getPhone());
                log.debug("Updated phone for consultant: {}", username);
            }

            if (updateRequest.getExpertiseArea() != null && !updateRequest.getExpertiseArea().isEmpty()) {
                consultant.setExpertiseArea(updateRequest.getExpertiseArea());
                log.debug("Updated expertise area for consultant: {}", username);
            }

            if (updateRequest.getExperienceYears() > 0) {
                consultant.setExperienceYears(updateRequest.getExperienceYears());
                log.debug("Updated experience years for consultant: {}", username);
            }

            if (updateRequest.getQualifications() != null && !updateRequest.getQualifications().isEmpty()) {
                consultant.setQualifications(updateRequest.getQualifications());
                log.debug("Updated qualifications for consultant: {}", username);
            }

            if (updateRequest.getSpecialization() != null && !updateRequest.getSpecialization().isEmpty()) {
                consultant.setSpecialization(updateRequest.getSpecialization());
                log.debug("Updated specialization for consultant: {}", username);
            }

            if (updateRequest.getBio() != null && !updateRequest.getBio().isEmpty()) {
                consultant.setBio(updateRequest.getBio());
                log.debug("Updated bio for consultant: {}", username);
            }

            // Update address if provided
            if (updateRequest.getAddress() != null) {
                Address address = consultant.getAddress();
                if (address == null) {
                    address = new Address();
                }

                if (updateRequest.getAddress().getStreet() != null) {
                    address.setStreet(updateRequest.getAddress().getStreet());
                }
                if (updateRequest.getAddress().getCity() != null) {
                    address.setCity(updateRequest.getAddress().getCity());
                }
                if (updateRequest.getAddress().getState() != null) {
                    address.setState(updateRequest.getAddress().getState());
                }
                if (updateRequest.getAddress().getPinCode() != null) {
                    address.setPinCode(updateRequest.getAddress().getPinCode());
                }
                if (updateRequest.getAddress().getCountry() != null) {
                    address.setCountry(updateRequest.getAddress().getCountry());
                }
                if (updateRequest.getAddress().getLatitude() != null) {
                    address.setLatitude(updateRequest.getAddress().getLatitude());
                }
                if (updateRequest.getAddress().getLongitude() != null) {
                    address.setLongitude(updateRequest.getAddress().getLongitude());
                }

                consultant.setAddress(address);
                log.debug("Address updated for consultant: {}", username);
            }

            consultantRepository.save(consultant);
            log.info("Consultant profile updated successfully for user: {}", username);
            return true;
        } catch (Exception e) {
            log.error("Error updating consultant profile for user: {}", username, e);
            return false;
        }
    }

    // Upload Profile Picture
    @Transactional
    public boolean uploadProfilePicture(String username, org.springframework.web.multipart.MultipartFile file) {
        log.info("Uploading profile picture for user: {}", username);
        try {
            Optional<Consultant> consultantOptional = this.getConsultantByUsername(username);
            if (consultantOptional.isEmpty()) {
                log.warn("Consultant not found for username: {}", username);
                return false;
            }

            Consultant consultant = consultantOptional.get();

            // Get project root directory
            String projectRoot = System.getProperty("user.dir");
            java.nio.file.Path uploadDir = java.nio.file.Paths.get(projectRoot, "uploads", "profile-pictures");

            // Create directories if they don't exist
            java.nio.file.Files.createDirectories(uploadDir);

            // Generate unique filename
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.nio.file.Path uploadPath = uploadDir.resolve(fileName);

            // Transfer file to the resolved path
            file.transferTo(uploadPath.toFile());

            log.info("Profile picture uploaded to: {}", uploadPath.toAbsolutePath());

            // Create or update UserProfilePicture entity
            UserProfilePicture profilePicture = consultant.getProfilePicture();
            if (profilePicture == null) {
                profilePicture = new UserProfilePicture();
            }

            profilePicture.setFilePath(uploadPath.toString());
            profilePicture.setFileName(fileName);
            profilePicture.setFileType(file.getContentType());
            profilePicture.setFileSize(file.getSize());
            profilePicture.setIsActive(true);
            profilePicture.setUploadedAt(java.time.LocalDateTime.now());
            profilePicture.setUser(consultant);

            consultant.setProfilePicture(profilePicture);
            consultantRepository.save(consultant);

            log.info("Profile picture saved successfully for user: {}", username);
            return true;
        } catch (Exception e) {
            log.error("Error uploading profile picture for user: {}", username, e);
            return false;
        }
    }

    // Get Profile Picture
    public org.springframework.core.io.Resource getProfilePicture(Long consultantId) {
        log.info("Fetching profile picture for consultant ID: {}", consultantId);
        try {
            Optional<Consultant> consultantOptional = this.getConsultantById(consultantId);
            if (consultantOptional.isEmpty()) {
                log.warn("Consultant not found for ID: {}", consultantId);
                return null;
            }

            Consultant consultant = consultantOptional.get();
            UserProfilePicture profilePicture = consultant.getProfilePicture();

            if (profilePicture == null || !profilePicture.getIsActive()) {
                log.warn("No active profile picture found for consultant ID: {}", consultantId);
                return null;
            }

            java.nio.file.Path filePath = java.nio.file.Paths.get(profilePicture.getFilePath());
            org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(
                    filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                log.info("Profile picture found for consultant ID: {}", consultantId);
                return resource;
            } else {
                log.warn("Profile picture file not found or not readable for consultant ID: {}", consultantId);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching profile picture for consultant ID: {}", consultantId, e);
            return null;
        }
    }

}
