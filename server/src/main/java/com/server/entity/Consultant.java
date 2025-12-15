package com.server.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import com.server.enumeration.VerificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Consultant extends User {
    @OneToOne(mappedBy = "consultant", cascade = CascadeType.ALL)
    @JsonBackReference
    @lombok.ToString.Exclude
    private VerificationDocument verificationDocument;
    @Column(columnDefinition = "TEXT")
    private String bio;

    private String expertiseArea;
    private int experienceYears;
    private String qualifications;
    private String specialization;
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus;

    @OneToMany(mappedBy = "consultant", cascade = CascadeType.ALL)
    @JsonBackReference(value = "consultation-consultant")
    @lombok.ToString.Exclude
    @Builder.Default
    private List<Consultation> consultations = new ArrayList<>();

    private LocalDateTime verifiedAt;
    @Value("false")
    private Boolean isActive;

}
