package com.server.service;

import java.util.List;
import java.util.Optional;

import com.server.dto.ConsultationRequestDTO;
import com.server.dto.farmerDTO.FarmerProfileUpdateRequest;
import com.server.entity.Consultation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.dto.FarmerDTO;
import com.server.entity.Address;
import com.server.entity.Farmer;
import com.server.repository.FarmerRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FarmerService {

	@Autowired
	private FarmerRepository farmerRepository;
	@Autowired
	private ConsultationService consultationService;

	public Optional<?> findById(Long id) {
		log.info("Fetching farmer with id: {}", id);
		Farmer farmer = farmerRepository.findById(id).orElseThrow(() -> {
			throw new RuntimeException("Farmer not found with id: " + id);
		});
		log.info("Farmer found: {}", farmer);
		FarmerDTO farmerDTO = new FarmerDTO();
		farmerDTO.setId(farmer.getId());
		farmerDTO.setFirstName(farmer.getFirstName());
		farmerDTO.setLastName(farmer.getLastName());
		farmerDTO.setEmail(farmer.getEmail());
		farmerDTO.setPhone(farmer.getPhone());
		farmerDTO.setRole(farmer.getRole());
		farmerDTO.setAddress(farmer.getAddress());
		farmerDTO.setFarmAreaHectares(farmer.getFarmAreaHectares());
		log.info("Converted Farmer to FarmerDTO: {}", farmerDTO);
		return Optional.of(farmerDTO);
	}

	/**
	 * Find farmer entity by ID (returns entity, not DTO)
	 * Used for internal service operations and authorization checks
	 */
	public Optional<Farmer> findFarmerEntityById(Long id) {
		log.info("Fetching farmer entity with id: {}", id);
		return farmerRepository.findById(id);
	}

	public Optional<?> findByEmail(String email) {
		log.info("Fetching farmer with email: {}", email);
		Farmer farmer = farmerRepository.findByEmail(email).orElseThrow(() -> {
			throw new RuntimeException("Farmer not found with username: " + email);
		});
		log.info("Farmer found: {}", farmer);
		FarmerDTO farmerDTO = new FarmerDTO();
		farmerDTO.setId(farmer.getId());
		farmerDTO.setFirstName(farmer.getFirstName());
		farmerDTO.setLastName(farmer.getLastName());
		farmerDTO.setEmail(farmer.getEmail());
		farmerDTO.setPhone(farmer.getPhone());
		farmerDTO.setRole(farmer.getRole());
		farmerDTO.setSoilType(farmer.getSoilType());
		farmerDTO.setAddress(farmer.getAddress());
		farmerDTO.setFarmAreaHectares(farmer.getFarmAreaHectares());

		// Add profile picture URL if exists
		if (farmer.getProfilePicture() != null && farmer.getProfilePicture().getIsActive()) {
			farmerDTO.setProfilePhotoUrl("http://localhost:8080/api/v1/farmers/profile-picture/" + farmer.getId());
		}

		log.info("Converted Farmer to FarmerDTO: {}", farmerDTO);
		return Optional.of(farmerDTO);
	}

	@Transactional
	public Optional<?> update(String username, FarmerProfileUpdateRequest request) {
		log.info("Updating farmer for username: {}", username);

		try {
			// Convert incoming payload into DTO
			// ObjectMapper mapper = new ObjectMapper();
			// log.info("Object Mapper:: ",mapper);
			// FarmerDTO updateDto = mapper.convertValue(farmerUpdate, FarmerDTO.class);
			// log.info("converted Object: {}",updateDto);

			// Find existing farmer by email/username
			Farmer farmer = farmerRepository.findByEmail(username)
					.orElseThrow(() -> new RuntimeException("Farmer not found with username: " + username));
			farmer.setFirstName(request.getFirstName());
			farmer.setLastName(request.getLastName());
			farmer.setPhone(request.getPhone());
			farmer.setSoilType(request.getSoilType());
			farmer.setFarmAreaHectares(request.getFarmAreaHectares());
			Address address = farmer.getAddress();
			if (address == null) {
				log.info("Farmer address is null");
				address = new Address();
			}

			address.setCity(request.getAddress().getCity());
			address.setStreet(request.getAddress().getStreet());
			address.setState(request.getAddress().getState());
			address.setPinCode(request.getAddress().getPinCode());
			address.setCountry(request.getAddress().getCountry());
			address.setLatitude(request.getAddress().getLatitude());
			address.setLongitude(request.getAddress().getLongitude());
			farmer.setAddress(address);

			Farmer saved = farmerRepository.save(farmer);
			log.info("Farmer updated successfully: {}", saved);

			// -----------------------------
			// PREPARE RESPONSE DTO
			// -----------------------------
			FarmerDTO result = new FarmerDTO();
			result.setId(saved.getId());
			result.setFirstName(saved.getFirstName());
			result.setLastName(saved.getLastName());
			result.setEmail(saved.getEmail());
			result.setPhone(saved.getPhone());
			result.setRole(saved.getRole());
			result.setAddress(saved.getAddress());
			result.setFarmAreaHectares(saved.getFarmAreaHectares());

			return Optional.of(result);

		} catch (Exception e) {
			log.error("Failed to update farmer for username {}: {}", username, e.getMessage(), e);
			throw new RuntimeException("Failed to update farmer: " + e.getMessage(), e);
		}
	}

	public Optional<?> createConsultationRequest(String username, ConsultationRequestDTO request) {
		log.info("Creating consultation request for username: {}", username);
		try {
			// Find farmer by email/username
			Farmer farmer = farmerRepository.findByEmail(username)
					.orElseThrow(() -> new RuntimeException("Farmer not found with username: " + username));

			// Delegate creation to ConsultationService
			Object createdRequest = consultationService.createConsultationRequest(farmer, request);
			log.info("Consultation request created: {}", createdRequest);
			log.info("Consultation request created successfully for username: {}", username);
			return Optional.of(createdRequest);

		} catch (Exception e) {
			log.error("Failed to create consultation request for username {}: {}", username, e.getMessage(), e);
			throw new RuntimeException("Failed to create consultation request: " + e.getMessage(), e);
		}
	}

	public Optional<List<Consultation>> getAllConsultationRequests(String username) {
		log.info("Fetching all consultation requests for username: {}", username);
		try {
			// Find farmer by email/username
			Farmer farmer = farmerRepository.findByEmail(username)
					.orElseThrow(() -> new RuntimeException("Farmer not found with username: " + username));

			// // Fetch consultations for the farmer
			// List<Consultation> consultations =
			// consultationService.getConsultationsByFarmer(farmer);
			// log.info("Found {} consultation requests for username: {}",
			// consultations.size(), username);
			// return Optional.of(consultations);
			return Optional.of(farmer.getConsultations());

		} catch (Exception e) {
			log.error("Failed to fetch consultation requests for username {}: {}", username, e.getMessage(), e);
			throw new RuntimeException("Failed to fetch consultation requests: " + e.getMessage(), e);
		}
	}

	// Upload Profile Picture
	@Transactional
	public boolean uploadProfilePicture(String username, org.springframework.web.multipart.MultipartFile file) {
		log.info("Uploading profile picture for farmer: {}", username);
		try {
			Farmer farmer = farmerRepository.findByEmail(username)
					.orElseThrow(() -> new RuntimeException("Farmer not found with username: " + username));

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
			com.server.entity.UserProfilePicture profilePicture = farmer.getProfilePicture();
			if (profilePicture == null) {
				profilePicture = new com.server.entity.UserProfilePicture();
			}

			profilePicture.setFilePath(uploadPath.toString());
			profilePicture.setFileName(fileName);
			profilePicture.setFileType(file.getContentType());
			profilePicture.setFileSize(file.getSize());
			profilePicture.setIsActive(true);
			profilePicture.setUploadedAt(java.time.LocalDateTime.now());
			profilePicture.setUser(farmer);

			farmer.setProfilePicture(profilePicture);
			farmerRepository.save(farmer);

			log.info("Profile picture saved successfully for farmer: {}", username);
			return true;
		} catch (Exception e) {
			log.error("Error uploading profile picture for farmer: {}", username, e);
			return false;
		}
	}

	// Get Profile Picture
	public org.springframework.core.io.Resource getProfilePicture(Long farmerId) {
		log.info("Fetching profile picture for farmer ID: {}", farmerId);
		try {
			Farmer farmer = farmerRepository.findById(farmerId)
					.orElseThrow(() -> new RuntimeException("Farmer not found with id: " + farmerId));

			com.server.entity.UserProfilePicture profilePicture = farmer.getProfilePicture();

			if (profilePicture == null || !profilePicture.getIsActive()) {
				log.warn("No active profile picture found for farmer ID: {}", farmerId);
				return null;
			}

			java.nio.file.Path filePath = java.nio.file.Paths.get(profilePicture.getFilePath());
			org.springframework.core.io.Resource resource = new org.springframework.core.io.UrlResource(
					filePath.toUri());

			if (resource.exists() && resource.isReadable()) {
				log.info("Profile picture found for farmer ID: {}", farmerId);
				return resource;
			} else {
				log.warn("Profile picture file not found or not readable for farmer ID: {}", farmerId);
				return null;
			}
		} catch (Exception e) {
			log.error("Error fetching profile picture for farmer ID: {}", farmerId, e);
			return null;
		}
	}
}
