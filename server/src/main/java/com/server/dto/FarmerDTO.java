package com.server.dto;

import com.server.entity.Address;

import com.server.enumeration.SoilType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FarmerDTO extends FarmerRegistrationResponse {
	private Address address;
	private SoilType soilType;
	private Double farmAreaHectares;
	private String profilePhotoUrl; // Profile picture URL
}
