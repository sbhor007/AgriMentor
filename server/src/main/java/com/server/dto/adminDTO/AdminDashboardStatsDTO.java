package com.server.dto.adminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardStatsDTO {
    private Long totalUsers;
    private Long totalFarmers;
    private Long totalConsultants;
    private Long activeConsultations;
    private Long approvedConsultations;
    private Long inProgressConsultations;
    private Long pendingRequests;
    private Double platformActivityRate;
    private UserGrowthStats userGrowth;
}
