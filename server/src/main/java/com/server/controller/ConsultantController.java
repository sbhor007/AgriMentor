package com.server.controller;

import com.server.dto.ConsultantDTO;
import com.server.dto.FarmVisitRequest;
import com.server.dto.consultantDTO.ConsultantResponse;
import com.server.dto.consultantDTO.ConsultantUpdateRequest;
import com.server.entity.VerificationDocument;
import com.server.repository.VerificationDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import com.server.response.ApiResponse;
import com.server.service.ConsultantService;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultants")
@Slf4j
public class ConsultantController {
    @Autowired
    private ConsultantService consultantService;

    @Autowired
    private VerificationDocumentRepository verificationDocumentRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllConsultants() {
        log.info("Received request to fetch all consultants");
        try {
            List<ConsultantResponse> consultants = consultantService.getAllConsultants();

            if (consultants.isEmpty()) {
                log.info("No consultants available");
                return ResponseEntity.ok(new ApiResponse<>(
                        HttpStatus.OK,
                        "No consultants available",
                        consultants));
            }

            log.info("Consultants fetched successfully. Total: {}", consultants.size());
            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK,
                    "Consultants retrieved successfully",
                    consultants));
        } catch (Exception e) {
            log.error("Error fetching consultants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching consultants: " + e.getMessage()));
        }
    }

    // get verified consultants

    // Get only verified consultants
    @GetMapping("/verified")
    public ResponseEntity<?> getVerifiedConsultants() {
        log.info("Received request to fetch verified consultants");
        try {
            List<ConsultantResponse> verifiedConsultants = consultantService.getVerifiedConsultants();

            if (verifiedConsultants.isEmpty()) {
                log.info("No verified consultants available");
                return ResponseEntity.ok(new ApiResponse<>(
                        HttpStatus.OK,
                        "No verified consultants available",
                        verifiedConsultants));
            }

            log.info("Verified consultants fetched successfully. Total: {}", verifiedConsultants.size());
            return ResponseEntity.ok(new ApiResponse<>(
                    HttpStatus.OK,
                    "Verified consultants retrieved successfully",
                    verifiedConsultants));
        } catch (Exception e) {
            log.error("Error fetching verified consultants", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error fetching verified consultants: " + e.getMessage()));
        }
    }

    // get consultant by username
    @GetMapping("/{username}")
    public ResponseEntity<?> getConsultantByUsername(@PathVariable String username) {
        log.info("Received request to get consultant by username: {}", username);
        try {
            Optional<?> consultantDTO = consultantService.getConsultantByUsername(username);
            log.info("Consultant fetched: {}", consultantDTO);
            if (consultantDTO.isPresent()) {
                log.info("Consultant retrieved successfully for username: {}", username);
                return ResponseEntity
                        .ok(new ApiResponse<>(HttpStatus.OK, "Consultant retrieved successfully", consultantDTO.get()));
            } else {
                log.info("Consultant not found for username: {}", username);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, "Consultant not found"));
            }
        } catch (Exception e) {
            log.error("Error fetching consultant for username: {}", username, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, e.getLocalizedMessage()));
        }
    }

    // get consulatant Profile
    @GetMapping("/profile")
    public ResponseEntity<?> getConsultantByUsername(Authentication authentication) {
        log.info("Received request to get consultant profile");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get consultant profile");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
        String username = authentication.getName();
        log.info("Attempting to get consultant profile for user: {}", username);
        Optional<?> consultantDTO = consultantService.getConsultantByUsername(username);
        log.info("Consultant profile fetched: {}", consultantDTO);
        if (consultantDTO.isPresent()) {
            log.info("Consultant profile retrieved successfully for user: {}", username);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK, "Consultant profile retrieved successfully", consultantDTO.get()));
        } else {
            log.info("Consultant profile not found for user: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, "Consultant profile not found"));
        }
    }

    // update consultant profile

    // Update Consultant Information
    // Update Consultant Information
    @PutMapping("/profile/update")
    public ResponseEntity<?> updateConsultantProfile(@RequestBody ConsultantUpdateRequest updateRequest,
            Authentication authentication) {
        log.info("Received request to update consultant profile");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to update consultant profile");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
        String username = authentication.getName();
        log.info("Attempting to update consultant profile for user: {}", username);
        try {
            boolean updated = consultantService.updateConsultantProfile(username, updateRequest);
            if (updated) {
                log.info("Consultant profile updated successfully for user: {}", username);
                return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Consultant profile updated successfully"));
            } else {
                log.info("Failed to update consultant profile for user: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Failed to update consultant profile"));
            }
        } catch (Exception e) {
            log.error("Error updating consultant profile for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getLocalizedMessage()));
        }
    }

    // getAllConsultants requests
    @GetMapping("/consultation/request/all")
    public ResponseEntity<?> getAllConsultationRequests(Authentication authentication) {
        log.info("Received request to get all consultation requests");
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get consultation requests");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
        String username = authentication.getName();
        log.info("Attempting to get consultation requests for user: {}", username);
        Optional<?> requests = consultantService.getAllConsultationRequests(username);
        log.info("Consultation requests fetched: {}", requests);
        if (requests.isPresent()) {
            log.info("Consultation requests retrieved successfully for user: {}", username);
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK, "Consultation requests retrieved successfully", requests.get()));
        } else {
            log.info("No consultation requests found for user: {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, "No consultation requests found"));
        }
    }

    // Accept Consultation Request
    @PutMapping("/consultation/request/{consultationId}/accept")
    public ResponseEntity<?> acceptConsultationRequest(@PathVariable Long consultationId,
            Authentication authentication) {
        log.info("Received request to accept consultation request with id: {}", consultationId);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to accept consultation request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
        String username = authentication.getName();
        log.info("Attempting to accept consultation request with id: {} for user: {}", consultationId, username);
        boolean accepted = consultantService.acceptConsultationRequest(username, consultationId);
        if (accepted) {
            log.info("Consultation request with id: {} accepted successfully for user: {}", consultationId, username);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Consultation request accepted successfully"));
        } else {
            log.info("Failed to accept consultation request with id: {} for user: {}", consultationId, username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Failed to accept consultation request"));
        }
    }

    // Reject Consultation Request
    @PutMapping("/consultation/request/{consultationId}/reject")
    public ResponseEntity<?> rejectConsultationRequest(@PathVariable Long consultationId,
            Authentication authentication) {
        log.info("Received request to reject consultation request with id: {}", consultationId);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to reject consultation request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
        String username = authentication.getName();
        log.info("Attempting to reject consultation request with id: {} for user: {}", consultationId, username);
        boolean rejected = consultantService.rejectConsultationRequest(username, consultationId);
        if (rejected) {
            log.info("Consultation request with id: {} rejected successfully for user: {}", consultationId, username);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Consultation request rejected successfully"));
        } else {
            log.info("Failed to reject consultation request with id: {} for user: {}", consultationId, username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Failed to reject consultation request"));
        }
    }

    // Schedule Consultation Visit
    @PutMapping("/consultation/request/{consultationId}/schedule-visit")
    public ResponseEntity<?> scheduleConsultationVisit(@PathVariable Long consultationId,
            @RequestBody FarmVisitRequest request, Authentication authentication) {
        log.info("Received request to schedule consultation visit for request id: {}", consultationId);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("Unauthorized access attempt to schedule consultation visit");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
        String username = authentication.getName();
        log.info("Attempting to schedule consultation visit for request id: {} by user: {}", consultationId, username);
        boolean scheduled = consultantService.scheduleConsultatinVisit(username, consultationId, request);
        if (scheduled) {
            log.info("Consultation visit scheduled successfully for request id: {} by user: {}", consultationId,
                    username);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Consultation visit scheduled successfully"));
        } else {
            log.info("Failed to schedule consultation visit for request id: {} by user: {}", consultationId, username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "Failed to schedule consultation visit"));
        }
    }

    /**
     * Download verification document by ID
     */
    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<?> downloadDocument(@PathVariable UUID documentId) {
        log.info("Downloading document with ID: {}", documentId);
        try {
            VerificationDocument document = verificationDocumentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found with ID: " + documentId));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getDocumentType() + ".pdf\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(document.getFileContent());
        } catch (Exception e) {
            log.error("Error downloading document with ID: {}", documentId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Document not found: " + e.getMessage());
        }
    }

    /**
     * View/Preview document (inline in browser)
     */
    @GetMapping("/documents/{documentId}/preview")
    public ResponseEntity<?> previewDocument(@PathVariable UUID documentId) {
        log.info("Previewing document with ID: {}", documentId);
        try {
            VerificationDocument document = verificationDocumentRepository.findById(documentId)
                    .orElseThrow(() -> new RuntimeException("Document not found"));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"" + document.getDocumentType() + ".pdf\"")
                    .body(document.getFileContent());
        } catch (Exception e) {
            log.error("Error previewing document", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
        }
    }

    /**
     * Upload profile picture for consultant
     */
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        log.info("Received request to upload profile picture");

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to upload profile picture");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }

        String username = authentication.getName();
        log.info("Uploading profile picture for user: {}", username);

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "File is empty"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "File must be an image"));
            }

            // Validate file size (5MB max)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, "File size must be less than 5MB"));
            }

            // Upload profile picture using service
            boolean uploaded = consultantService.uploadProfilePicture(username, file);

            if (uploaded) {
                log.info("Profile picture uploaded successfully for user: {}", username);
                return ResponseEntity.ok(
                        new ApiResponse<>(HttpStatus.OK, "Profile picture uploaded successfully"));
            } else {
                log.warn("Failed to upload profile picture for user: {}", username);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR,
                                "Failed to upload profile picture"));
            }
        } catch (Exception e) {
            log.error("Error uploading profile picture for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Error uploading profile picture: " + e.getMessage()));
        }
    }

    /**
     * Download/view profile picture by consultant ID
     */
    @GetMapping("/profile-picture/{consultantId}")
    public ResponseEntity<?> getProfilePicture(@PathVariable Long consultantId) {
        log.info("Fetching profile picture for consultant ID: {}", consultantId);

        try {
            Resource resource = consultantService.getProfilePicture(consultantId);

            if (resource == null || !resource.exists()) {
                log.warn("Profile picture not found for consultant ID: {}", consultantId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Profile picture not found");
            }

            // Determine content type
            String contentType = "image/jpeg"; // default
            try {
                Path path = resource.getFile().toPath();
                contentType = Files.probeContentType(path);
                if (contentType == null) {
                    contentType = "image/jpeg";
                }
            } catch (IOException e) {
                log.warn("Could not determine file type for consultant ID: {}", consultantId);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error fetching profile picture for consultant ID: {}", consultantId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching profile picture: " + e.getMessage());
        }
    }

}