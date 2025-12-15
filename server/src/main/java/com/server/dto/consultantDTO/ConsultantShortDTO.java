package com.server.dto.consultantDTO;




import com.server.dto.verificationDocumentDTOs.VerificationDocumentDTO;
import com.server.enumeration.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultantShortDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String expertiseArea;
    private VerificationStatus verificationStatus;
    private VerificationDocumentDTO verificationDocument;
}
