package com.server.service;

import com.server.dto.ConsultationReportDTO.ConsultationReportRequestDTO;
import com.server.dto.ConsultationReportDTO.ConsultationReportResponseDTO;
import com.server.dto.ConsultationReportDTO.ConsultationReportSummaryDTO;
import com.server.dto.ConsultationReportDTO.ReportAttachmentDTO;
import com.server.entity.Consultation;
import com.server.entity.ConsultationReport;
import com.server.entity.ReportAttachment;
import com.server.repository.ConsultationReportRepository;
import com.server.repository.ConsultationRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer for ConsultationReport entity
 * Handles business logic, DTO mapping, and logging for consultation reports
 */
@Service
@Slf4j
public class ConsultationReportService {

    @Autowired
    private ConsultationReportRepository consultationReportRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    /**
     * Create a new consultation report
     * 
     * @param requestDTO the report request data
     * @return the created report as response DTO
     */
    @Transactional
    public Optional<ConsultationReportResponseDTO> createReport(ConsultationReportRequestDTO requestDTO) {
        log.info("Creating consultation report for consultation ID: {}", requestDTO.getConsultationId());

        try {
            // Validate consultation exists
            Consultation consultation = consultationRepository.findById(requestDTO.getConsultationId())
                    .orElseThrow(() -> {
                        log.error("Consultation not found with ID: {}", requestDTO.getConsultationId());
                        return new RuntimeException(
                                "Consultation not found with ID: " + requestDTO.getConsultationId());
                    });

            log.debug("Found consultation: {} for farmer: {} and consultant: {}",
                    consultation.getTopic(),
                    consultation.getFarmer().getEmail(),
                    consultation.getConsultant().getEmail());

            // Create report entity
            ConsultationReport report = new ConsultationReport();
            report.setConsultation(consultation);
            report.setReportText(requestDTO.getReportText());
            report.setRecommendations(requestDTO.getRecommendations());
            report.setIdentifiedIssue(requestDTO.getIdentifiedIssue());
            report.setFollowUpDate(requestDTO.getFollowUpDate());

            // Save report
            ConsultationReport savedReport = consultationReportRepository.save(report);
            log.info("Successfully created consultation report with ID: {} for consultation ID: {}",
                    savedReport.getId(), requestDTO.getConsultationId());

            // Convert to DTO and return
            ConsultationReportResponseDTO responseDTO = mapToResponseDTO(savedReport);
            log.debug("Mapped report to response DTO with {} attachments",
                    responseDTO.getAttachments() != null ? responseDTO.getAttachments().size() : 0);

            return Optional.of(responseDTO);

        } catch (Exception e) {
            log.error("Error creating consultation report for consultation ID: {}. Error: {}",
                    requestDTO.getConsultationId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Update an existing consultation report
     * 
     * @param reportId   the report ID to update
     * @param requestDTO the updated report data
     * @return the updated report as response DTO
     */
    @Transactional
    public Optional<ConsultationReportResponseDTO> updateReport(Long reportId,
            ConsultationReportRequestDTO requestDTO) {
        log.info("Updating consultation report with ID: {}", reportId);

        try {
            ConsultationReport report = consultationReportRepository.findById(reportId)
                    .orElseThrow(() -> {
                        log.error("Consultation report not found with ID: {}", reportId);
                        return new RuntimeException("Consultation report not found with ID: " + reportId);
                    });

            log.debug("Found existing report for consultation: {}", report.getConsultation().getTopic());

            // Update fields
            if (requestDTO.getReportText() != null) {
                report.setReportText(requestDTO.getReportText());
                log.debug("Updated report text");
            }
            if (requestDTO.getRecommendations() != null) {
                report.setRecommendations(requestDTO.getRecommendations());
                log.debug("Updated recommendations");
            }
            if (requestDTO.getIdentifiedIssue() != null) {
                report.setIdentifiedIssue(requestDTO.getIdentifiedIssue());
                log.debug("Updated identified issue");
            }
            if (requestDTO.getFollowUpDate() != null) {
                report.setFollowUpDate(requestDTO.getFollowUpDate());
                log.debug("Updated follow-up date to: {}", requestDTO.getFollowUpDate());
            }

            // Save updated report
            ConsultationReport updatedReport = consultationReportRepository.save(report);
            log.info("Successfully updated consultation report with ID: {}", reportId);

            return Optional.of(mapToResponseDTO(updatedReport));

        } catch (Exception e) {
            log.error("Error updating consultation report with ID: {}. Error: {}",
                    reportId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get a consultation report by ID
     * 
     * @param reportId the report ID
     * @return the report as response DTO
     */
    public Optional<ConsultationReportResponseDTO> getReportById(Long reportId) {
        log.info("Fetching consultation report with ID: {}", reportId);

        try {
            Optional<ConsultationReport> report = consultationReportRepository.findById(reportId);

            if (report.isPresent()) {
                log.info("Found consultation report with ID: {}", reportId);
                return Optional.of(mapToResponseDTO(report.get()));
            } else {
                log.warn("Consultation report not found with ID: {}", reportId);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Error fetching consultation report with ID: {}. Error: {}",
                    reportId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all reports for a specific consultation
     * 
     * @param consultationId the consultation ID
     * @return list of reports as response DTOs
     */
    public List<ConsultationReportResponseDTO> getReportsByConsultationId(Long consultationId) {
        log.info("Fetching all reports for consultation ID: {}", consultationId);

        try {
            List<ConsultationReport> reports = consultationReportRepository.findByConsultationId(consultationId);
            log.info("Found {} reports for consultation ID: {}", reports.size(), consultationId);

            return reports.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching reports for consultation ID: {}. Error: {}",
                    consultationId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get the latest report for a consultation
     * 
     * @param consultationId the consultation ID
     * @return the latest report as response DTO
     */
    public Optional<ConsultationReportResponseDTO> getLatestReportByConsultationId(Long consultationId) {
        log.info("Fetching latest report for consultation ID: {}", consultationId);

        try {
            Optional<ConsultationReport> report = consultationReportRepository
                    .findLatestByConsultationId(consultationId);

            if (report.isPresent()) {
                log.info("Found latest report with ID: {} for consultation ID: {}",
                        report.get().getId(), consultationId);
                return Optional.of(mapToResponseDTO(report.get()));
            } else {
                log.warn("No reports found for consultation ID: {}", consultationId);
                return Optional.empty();
            }

        } catch (Exception e) {
            log.error("Error fetching latest report for consultation ID: {}. Error: {}",
                    consultationId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all reports created by a consultant
     * 
     * @param consultantId the consultant ID
     * @return list of reports as summary DTOs
     */
    public List<ConsultationReportSummaryDTO> getReportsByConsultantId(Long consultantId) {
        log.info("Fetching all reports for consultant ID: {}", consultantId);

        try {
            List<ConsultationReport> reports = consultationReportRepository.findByConsultantId(consultantId);
            log.info("Found {} reports for consultant ID: {}", reports.size(), consultantId);

            return reports.stream()
                    .map(this::mapToSummaryDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching reports for consultant ID: {}. Error: {}",
                    consultantId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all reports for a farmer
     * 
     * @param farmerId the farmer ID
     * @return list of reports as summary DTOs
     */
    public List<ConsultationReportSummaryDTO> getReportsByFarmerId(Long farmerId) {
        log.info("Fetching all reports for farmer ID: {}", farmerId);

        try {
            List<ConsultationReport> reports = consultationReportRepository.findByFarmerId(farmerId);
            log.info("Found {} reports for farmer ID: {}", reports.size(), farmerId);

            return reports.stream()
                    .map(this::mapToSummaryDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching reports for farmer ID: {}. Error: {}",
                    farmerId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get upcoming follow-ups for a consultant
     * 
     * @param consultantId the consultant ID
     * @return list of reports with upcoming follow-ups
     */
    public List<ConsultationReportSummaryDTO> getUpcomingFollowUpsByConsultant(Long consultantId) {
        log.info("Fetching upcoming follow-ups for consultant ID: {}", consultantId);

        try {
            LocalDateTime now = LocalDateTime.now();
            List<ConsultationReport> reports = consultationReportRepository
                    .findUpcomingFollowUpsByConsultant(consultantId, now);

            log.info("Found {} upcoming follow-ups for consultant ID: {}", reports.size(), consultantId);

            return reports.stream()
                    .map(this::mapToSummaryDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching upcoming follow-ups for consultant ID: {}. Error: {}",
                    consultantId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get all upcoming follow-ups
     * 
     * @return list of reports with upcoming follow-ups
     */
    public List<ConsultationReportSummaryDTO> getAllUpcomingFollowUps() {
        log.info("Fetching all upcoming follow-ups");

        try {
            LocalDateTime now = LocalDateTime.now();
            List<ConsultationReport> reports = consultationReportRepository.findUpcomingFollowUps(now);

            log.info("Found {} upcoming follow-ups", reports.size());

            return reports.stream()
                    .map(this::mapToSummaryDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching all upcoming follow-ups. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get reports within a date range
     * 
     * @param startDate start of date range
     * @param endDate   end of date range
     * @return list of reports as summary DTOs
     */
    public List<ConsultationReportSummaryDTO> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching reports created between {} and {}", startDate, endDate);

        try {
            List<ConsultationReport> reports = consultationReportRepository
                    .findByCreatedAtBetween(startDate, endDate);

            log.info("Found {} reports in the specified date range", reports.size());

            return reports.stream()
                    .map(this::mapToSummaryDTO)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error fetching reports by date range. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Delete a consultation report
     * 
     * @param reportId the report ID to delete
     * @return true if deleted successfully
     */
    @Transactional
    public boolean deleteReport(Long reportId) {
        log.info("Deleting consultation report with ID: {}", reportId);

        try {
            if (!consultationReportRepository.existsById(reportId)) {
                log.warn("Cannot delete - consultation report not found with ID: {}", reportId);
                return false;
            }

            consultationReportRepository.deleteById(reportId);
            log.info("Successfully deleted consultation report with ID: {}", reportId);
            return true;

        } catch (Exception e) {
            log.error("Error deleting consultation report with ID: {}. Error: {}",
                    reportId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Get count of reports for a consultation
     * 
     * @param consultationId the consultation ID
     * @return count of reports
     */
    public Long getReportCountByConsultationId(Long consultationId) {
        log.info("Counting reports for consultation ID: {}", consultationId);

        try {
            Long count = consultationReportRepository.countByConsultationId(consultationId);
            log.info("Found {} reports for consultation ID: {}", count, consultationId);
            return count;

        } catch (Exception e) {
            log.error("Error counting reports for consultation ID: {}. Error: {}",
                    consultationId, e.getMessage(), e);
            throw e;
        }
    }

    // ==================== DTO Mapping Methods ====================

    /**
     * Map ConsultationReport entity to ResponseDTO
     * 
     * @param report the report entity
     * @return the response DTO
     */
    private ConsultationReportResponseDTO mapToResponseDTO(ConsultationReport report) {
        log.debug("Mapping ConsultationReport entity to ResponseDTO for report ID: {}", report.getId());

        Consultation consultation = report.getConsultation();

        List<ReportAttachmentDTO> attachmentDTOs = report.getAttachments() != null
                ? report.getAttachments().stream()
                        .map(this::mapAttachmentToDTO)
                        .collect(Collectors.toList())
                : List.of();

        return ConsultationReportResponseDTO.builder()
                .id(report.getId())
                .consultationId(consultation.getId())
                .consultationTopic(consultation.getTopic())
                .farmerName(consultation.getFarmer().getFirstName() + " " + consultation.getFarmer().getLastName())
                .consultantName(
                        consultation.getConsultant().getFirstName() + " " + consultation.getConsultant().getLastName())
                .reportText(report.getReportText())
                .recommendations(report.getRecommendations())
                .identifiedIssue(report.getIdentifiedIssue())
                .followUpDate(report.getFollowUpDate())
                .createdAt(report.getCreatedAt())
                .attachments(attachmentDTOs)
                .build();
    }

    /**
     * Map ConsultationReport entity to SummaryDTO
     * 
     * @param report the report entity
     * @return the summary DTO
     */
    private ConsultationReportSummaryDTO mapToSummaryDTO(ConsultationReport report) {
        log.debug("Mapping ConsultationReport entity to SummaryDTO for report ID: {}", report.getId());

        return ConsultationReportSummaryDTO.builder()
                .id(report.getId())
                .consultationId(report.getConsultation().getId())
                .consultationTopic(report.getConsultation().getTopic())
                .identifiedIssue(report.getIdentifiedIssue())
                .followUpDate(report.getFollowUpDate())
                .createdAt(report.getCreatedAt())
                .attachmentCount(report.getAttachments() != null ? report.getAttachments().size() : 0)
                .build();
    }

    /**
     * Map ReportAttachment entity to DTO
     * 
     * @param attachment the attachment entity
     * @return the attachment DTO
     */
    private ReportAttachmentDTO mapAttachmentToDTO(ReportAttachment attachment) {
        return ReportAttachmentDTO.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .build();
    }
}
