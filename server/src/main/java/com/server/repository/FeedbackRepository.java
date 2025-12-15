package com.server.repository;

import com.server.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    
    Optional<Feedback> findByConsultationId(Long consultationId);
    
    boolean existsByConsultationId(Long consultationId);
    
    List<Feedback> findByConsultantId(Long consultantId);
    
    List<Feedback> findByFarmerId(Long farmerId);
    
    @Query("SELECT AVG(f.ratingOverall) FROM Feedback f WHERE f.consultant.id = :consultantId")
    Double getAverageRatingByConsultantId(@Param("consultantId") Long consultantId);
    
    Long countByConsultantId(Long consultantId);
}
