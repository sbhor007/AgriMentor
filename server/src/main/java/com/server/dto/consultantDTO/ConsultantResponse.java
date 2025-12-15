package com.server.dto.consultantDTO;

import com.server.dto.AddressDTO;
import com.server.dto.verificationDocumentDTOs.VerificationDocumentDTO;
import com.server.entity.VerificationDocument;
import com.server.enumeration.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultantResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String expertiseArea;
    private int experienceYears;
    private String qualifications;
    private String specialization;
    private VerificationStatus verificationStatus;
    private VerificationDocumentDTO verificationDocument;
    private Boolean isActive;
    private String bio;
    private AddressDTO address;
    private LocalDateTime verifiedAt;
    private String profilePhotoUrl; // Profile picture URL
}
