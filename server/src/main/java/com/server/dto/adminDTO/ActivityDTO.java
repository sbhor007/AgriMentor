package com.server.dto.adminDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private String type; // USER_REGISTERED, CONSULTATION_APPROVED, CONSULTATION_CREATED, etc.
    private String description;
    private String username;
    private String userRole;
    private LocalDateTime timestamp;
}
