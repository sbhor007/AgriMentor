package com.server.dto.farmerDTO;

import com.server.dto.AddressDTO;
import com.server.enumeration.SoilType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private SoilType soilType;
    private Double farmAreaHectares;
    private AddressDTO address;
}
