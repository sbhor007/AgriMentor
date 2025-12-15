package com.server.dto.consultantDTO;

import com.server.dto.AddressDTO;
import lombok.*;
//import jakarta.validation.constraints.Min;
//import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultantUpdateRequest {

//    @NotBlank(message = "First name cannot be blank")
    private String firstName;

//    @NotBlank(message = "Last name cannot be blank")
    private String lastName;

//    @NotBlank(message = "Phone number cannot be blank")
    private String phone;

//    @NotBlank(message = "Expertise area cannot be blank")
    private String expertiseArea;

//    @Min(value = 0, message = "Experience years must be greater than or equal to 0")
    private int experienceYears;

//    @NotBlank(message = "Qualifications cannot be blank")
    private String qualifications;

//    @NotBlank(message = "Specialization cannot be blank")
    private String specialization;

//    @Valid
    private AddressDTO address;
    private String bio;
}
