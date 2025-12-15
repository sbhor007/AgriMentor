package com.server.controller;

import java.util.Optional;

import com.server.dto.ConsultationRequestDTO;
import com.server.dto.FarmerDTO;
import com.server.dto.farmerDTO.FarmerProfileUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.server.response.ApiResponse;
import com.server.service.FarmerService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/farmers")
@Slf4j
public class FarmerController {

    @Autowired
    private FarmerService farmerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getFarmerById(@PathVariable Long id, Authentication authentication) {
        log.info("Received request to get farmer with id: {}", id);
        log.info("Authentication details: {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("Unauthorized access attempt to get farmer with id: {}", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }
        Optional<?> farmerDTO = farmerService.findById(id);

        if (farmerDTO.isPresent()) {
            log.info("Farmer found with id: {}", id);
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Farmer retrieved successfully", farmerDTO.get()));
        } else {
            log.info("Farmer not found with id: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, "Farmer not found"));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getFarmer(Authentication authentication) {
        log.info("Received request to get farmer with authentication : {}", authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to get farmer ");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }

        log.info("Extracted farmer user:name {}", authentication.getName());
        Optional<?> farmerDTO = farmerService.findByEmail(authentication.getName());

        if (farmerDTO.isPresent()) {
            log.info("Farmer found with email/username: {}", authentication.getName());
            return ResponseEntity.ok().body(
                    new ApiResponse<>(HttpStatus.OK, "Farmer retrieved successfully", farmerDTO.get()));
        } else {
            log.info("Farmer not found with email/username: {}", authentication.getName());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<String>(HttpStatus.NOT_FOUND, "Farmer not found"));
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateFarmer(
            @RequestBody FarmerProfileUpdateRequest request,
            Authentication authentication) {

        log.info("Received update request: {}", request);

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }

        String username = authentication.getName();
        log.info("Updating farmer for: {}", username);

        Optional<?> updated = farmerService.update(username, request);

        if (updated.isPresent()) {
            return ResponseEntity.ok(
                    new ApiResponse<>(HttpStatus.OK, "Farmer updated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(HttpStatus.NOT_FOUND, "Farmer not found"));
        }
    }

    @PostMapping("/consultation/request")
    public ResponseEntity<?> createConsultationRequest(@RequestBody ConsultationRequestDTO request,
            Authentication authentication) {
        log.info("Received request to create consultation request: {}", request);
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Unauthorized access attempt to create consultation request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<String>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }

        String username = authentication.getName();
        log.info("Attempting to create consultation request for user: {}", username);

        Optional<?> createdRequest = farmerService.createConsultationRequest(username, request);

        if (createdRequest.isPresent()) {
            log.info("Consultation request created successfully for user: {}", username);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Consultation request created successfully",
                    createdRequest.get()));
        } else {
            log.error("Failed to create consultation request for user: {}", username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to create consultation request"));
        }
    }

    // get all consultation requests of farmer
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
        Optional<?> requests = farmerService.getAllConsultationRequests(username);
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

    // Profile Picture Upload
    @PostMapping("/profile-picture")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            Authentication authentication) {

        log.info("📸 Received profile picture upload request");

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("❌ Unauthorized profile picture upload attempt");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "File is empty"));
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "Only image files are allowed"));
        }

        // Validate file size (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(HttpStatus.BAD_REQUEST, "File size must be less than 5MB"));
        }

        String username = authentication.getName();
        log.info("📤 Uploading profile picture for farmer: {}", username);

        boolean uploaded = farmerService.uploadProfilePicture(username, file);

        if (uploaded) {
            log.info("✅ Profile picture uploaded successfully for: {}", username);
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, "Profile picture uploaded successfully"));
        } else {
            log.error("❌ Failed to upload profile picture for: {}", username);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload profile picture"));
        }
    }

    // Profile Picture Download
    @GetMapping("/profile-picture/{farmerId}")
    public ResponseEntity<?> getProfilePicture(@PathVariable Long farmerId) {
        log.info("🖼️ Fetching profile picture for farmer ID: {}", farmerId);

        try {
            org.springframework.core.io.Resource resource = farmerService.getProfilePicture(farmerId);

            if (resource == null) {
                return ResponseEntity.notFound().build();
            }

            // Determine content type
            String contentType = "image/jpeg"; // default
            try {
                contentType = java.nio.file.Files.probeContentType(
                        java.nio.file.Paths.get(resource.getFile().getAbsolutePath()));
            } catch (Exception e) {
                log.warn("Could not determine file type for farmer {}", farmerId);
            }

            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, contentType)
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error retrieving profile picture for farmer {}: {}", farmerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
