package com.server.dto;

import jakarta.persistence.Column;
import org.springframework.web.multipart.MultipartFile;

import com.server.enumeration.Role;
import com.server.enumeration.VerificationStatus;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CunsultantRegisterRequest {
	private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private Role role;
    private String expertiseArea;
    private String experienceYears;
    private String qualifications;
    private String specialization;
    private String bio;
    private MultipartFile verificationDocument;
    private AddressDTO address;
    
    
}
