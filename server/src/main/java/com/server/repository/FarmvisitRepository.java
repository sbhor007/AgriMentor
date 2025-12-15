package com.server.repository;

import com.server.entity.Farmvisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmvisitRepository extends JpaRepository<Farmvisit, Long> {

    Optional<Farmvisit> findByConsultationIdAndConsultationConsultantEmail(Long consultationId, String username);

    List<Farmvisit> findAllByConsultationId(Long consultationId);

    Optional<List<Farmvisit>> findByConsultationIdAndConsultation_Consultant_Email(Long consultationId,
            String username);

    // Get all farm visits for a consultant
    List<Farmvisit> findByConsultation_Consultant_Email(String consultantEmail);
}
