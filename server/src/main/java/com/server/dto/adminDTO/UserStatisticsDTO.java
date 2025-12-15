package com.server.dto.adminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatisticsDTO {
    private Long totalUsers;
    private Long totalFarmers;
    private Long totalConsultants;
    private Long totalAdmins;
    private Long activeUsers;
    private Long inactiveUsers;
    private Long verifiedUsers;
    private Long unverifiedUsers;
}
