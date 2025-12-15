package com.server.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class UserProfilePicture  {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String filePath;
	    private String fileName;
	    private String fileType;
	    private Long fileSize;
	    private Boolean isActive;
	    private LocalDateTime uploadedAt;
    @OneToOne(mappedBy = "profilePicture")
    @JsonBackReference
    private User user;
}
