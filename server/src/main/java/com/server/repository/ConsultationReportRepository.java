package com.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.server.entity.ConsultationReport;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for ConsultationReport entity
 * Provides custom query methods for various report retrieval scenarios
 */
@Repository
public interface ConsultationReportRepository extends JpaRepository<ConsultationReport, Long> {

    /**
     * Find all reports for a specific consultation
     * 
     * @param consultationId the consultation ID
     * @return list of consultation reports
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.consultation.id = :consultationId ORDER BY cr.createdAt DESC")
    List<ConsultationReport> findByConsultationId(@Param("consultationId") Long consultationId);

    /**
     * Find the latest report for a consultation
     * 
     * @param consultationId the consultation ID
     * @return optional consultation report
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.consultation.id = :consultationId ORDER BY cr.createdAt DESC")
    Optional<ConsultationReport> findLatestByConsultationId(@Param("consultationId") Long consultationId);

    /**
     * Find all reports created by a specific consultant
     * 
     * @param consultantId the consultant ID
     * @return list of consultation reports
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.consultation.consultant.id = :consultantId ORDER BY cr.createdAt DESC")
    List<ConsultationReport> findByConsultantId(@Param("consultantId") Long consultantId);

    /**
     * Find all reports for a specific farmer
     * 
     * @param farmerId the farmer ID
     * @return list of consultation reports
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.consultation.farmer.id = :farmerId ORDER BY cr.createdAt DESC")
    List<ConsultationReport> findByFarmerId(@Param("farmerId") Long farmerId);

    /**
     * Find reports with follow-up dates within a date range
     * 
     * @param startDate start of date range
     * @param endDate   end of date range
     * @return list of consultation reports
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.followUpDate BETWEEN :startDate AND :endDate ORDER BY cr.followUpDate ASC")
    List<ConsultationReport> findByFollowUpDateBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Find reports created within a date range
     * 
     * @param startDate start of date range
     * @param endDate   end of date range
     * @return list of consultation reports
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.createdAt BETWEEN :startDate AND :endDate ORDER BY cr.createdAt DESC")
    List<ConsultationReport> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Count reports for a specific consultation
     * 
     * @param consultationId the consultation ID
     * @return count of reports
     */
    @Query("SELECT COUNT(cr) FROM ConsultationReport cr WHERE cr.consultation.id = :consultationId")
    Long countByConsultationId(@Param("consultationId") Long consultationId);

    /**
     * Find reports with upcoming follow-ups (after current date)
     * 
     * @param currentDate the current date
     * @return list of consultation reports
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.followUpDate > :currentDate ORDER BY cr.followUpDate ASC")
    List<ConsultationReport> findUpcomingFollowUps(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find reports by consultant with follow-up dates
     * 
     * @param consultantId the consultant ID
     * @param currentDate  the current date
     * @return list of consultation reports
     */
    @Query("SELECT cr FROM ConsultationReport cr WHERE cr.consultation.consultant.id = :consultantId AND cr.followUpDate > :currentDate ORDER BY cr.followUpDate ASC")
    List<ConsultationReport> findUpcomingFollowUpsByConsultant(@Param("consultantId") Long consultantId,
            @Param("currentDate") LocalDateTime currentDate);
}
