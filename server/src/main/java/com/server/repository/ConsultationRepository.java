package com.server.repository;

import com.server.entity.Consultation;
import com.server.enumeration.ConsultationRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {

    // ==================== Farmer-based Queries ====================
    List<Consultation> findByFarmerId(Long id);

    List<Consultation> findByFarmerIdAndConsultationRequestStatus(Long farmerId, ConsultationRequestStatus status);

    Long countByFarmerId(Long farmerId);

    // ==================== Consultant-based Queries ====================
    List<Consultation> findByConsultantId(Long consultantId);

    List<Consultation> findByConsultantIdAndConsultationRequestStatus(Long consultantId,
            ConsultationRequestStatus status);

    Long countByConsultantId(Long consultantId);

    Long countByConsultantIdAndConsultationRequestStatus(Long consultantId, ConsultationRequestStatus status);

    // ==================== Status-based Queries ====================
    List<Consultation> findByConsultationRequestStatus(ConsultationRequestStatus status);

    Long countByConsultationRequestStatus(ConsultationRequestStatus status);

    // ==================== Date-based Queries ====================
    List<Consultation> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Consultation> findByCreatedAtAfter(LocalDateTime date);

    Long countByCreatedAtAfter(LocalDateTime date);

    List<Consultation> findByUpdatedAtAfter(LocalDateTime date);

    // ==================== Crop-based Queries ====================
    List<Consultation> findByCropId(Long cropId);

    Long countByCropId(Long cropId);

    // ==================== Recent and Ordering Queries ====================
    List<Consultation> findTop10ByOrderByCreatedAtDesc();

    List<Consultation> findByFarmerIdOrderByCreatedAtDesc(Long farmerId);

    List<Consultation> findByConsultantIdOrderByCreatedAtDesc(Long consultantId);

    // ==================== Combined Complex Queries ====================
    List<Consultation> findByFarmerIdAndConsultationRequestStatusOrderByCreatedAtDesc(
            Long farmerId, ConsultationRequestStatus status);

    List<Consultation> findByConsultantIdAndConsultationRequestStatusOrderByCreatedAtDesc(
            Long consultantId, ConsultationRequestStatus status);

    List<Consultation> findByConsultationRequestStatusAndCreatedAtAfter(
            ConsultationRequestStatus status, LocalDateTime date);

    // ==================== Existence Checks ====================
    boolean existsByFarmerIdAndConsultantIdAndConsultationRequestStatus(
            Long farmerId, Long consultantId, ConsultationRequestStatus status);

    // ==================== Custom JPQL Queries ====================
    @Query("SELECT c FROM Consultation c WHERE c.consultationRequestStatus IN :statuses")
    List<Consultation> findByStatusIn(@Param("statuses") List<ConsultationRequestStatus> statuses);

    @Query("SELECT c FROM Consultation c WHERE c.farmer.id = :farmerId AND c.consultationRequestStatus IN :statuses")
    List<Consultation> findByFarmerIdAndStatusIn(
            @Param("farmerId") Long farmerId,
            @Param("statuses") List<ConsultationRequestStatus> statuses);

    @Query("SELECT c FROM Consultation c WHERE c.consultant.id = :consultantId AND c.consultationRequestStatus IN :statuses")
    List<Consultation> findByConsultantIdAndStatusIn(
            @Param("consultantId") Long consultantId,
            @Param("statuses") List<ConsultationRequestStatus> statuses);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.farmer.id = :farmerId AND c.consultationRequestStatus IN :statuses")
    Long countByFarmerIdAndStatusIn(
            @Param("farmerId") Long farmerId,
            @Param("statuses") List<ConsultationRequestStatus> statuses);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.consultant.id = :consultantId AND c.consultationRequestStatus IN :statuses")
    Long countByConsultantIdAndStatusIn(
            @Param("consultantId") Long consultantId,
            @Param("statuses") List<ConsultationRequestStatus> statuses);
}
