package com.server.dto.verificationDocumentDTOs;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerificationDocumentDTO {
    private UUID id;
    private String documentType;        // e.g., "ID Proof", "Qualification Certificate"
    private String documentUrl;
}
