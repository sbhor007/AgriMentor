package com.server.repository;

import java.util.List;
import java.util.Optional;

import com.server.enumeration.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.entity.Consultant;

@Repository

public interface ConsultantRepository extends JpaRepository<Consultant, Long> {
    Optional<Consultant> findByEmail(String email);

    List<Consultant> findByVerificationStatus(VerificationStatus verificationStatus);

}
