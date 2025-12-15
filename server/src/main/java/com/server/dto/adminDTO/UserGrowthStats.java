package com.server.dto.adminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserGrowthStats {
    private Long farmersThisMonth;
    private Long consultantsThisMonth;
    private Double farmerGrowthPercentage;
    private Double consultantGrowthPercentage;
}
