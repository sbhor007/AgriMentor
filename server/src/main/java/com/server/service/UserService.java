package com.server.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.dto.RegisterRequest;
import com.server.entity.*;
import com.server.repository.*;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class UserService {
	@Autowired
	 private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


     public String registerUser(RegisterRequest request) {
    	 System.out.println("User Service Test" + request.toString());
    	 
    	// Check if email exists
    	 if (userRepository.existsByEmail(request.getEmail())) {
             return "Email already registered!";
         }
    	 
    	  // Check if phone number exists
         if (userRepository.existsByPhone(request.getPhone())) {
             return "Phone number already registered!";
         }
    	 
    	 User user = new User();
    	 user.setFirstName(request.getFirstName());
    	 user.setLastName(request.getLastName());
    	 user.setEmail(request.getEmail());
    	 user.setPhone(request.getPhone());
    	 user.setPassword(request.getPassword());
    	 user.setRole(request.getRole());
    	 userRepository.save(user);
		 return "User registered successfully";
    	 
     }

	 public Optional<?> findUserById(Long userId) {
		// TODO Auto-generated method stub
		return null;
	 }
	 
	 
	 @Transactional
	 public void updatePassword(String email, String newPassword) {
		 log.error("Update password block");
		    User user = userRepository.findByEmail(email)
		            .orElseThrow(() -> new RuntimeException("User not found"));

		    user.setPassword(passwordEncoder.encode(newPassword));
		    userRepository.save(user);
		}

	 
	 
}
