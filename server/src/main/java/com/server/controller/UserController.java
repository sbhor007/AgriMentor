package com.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.dto.RegisterRequest;
import com.server.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
	@Autowired
    private UserService userService;


	
	@GetMapping("/test")
	public void Test(Authentication authentication) {
		log.info("User Controller Test accessed by: {}", authentication);
		System.out.println(authentication.toString());
			 	 System.out.println("User Controller Test");
	}
	
//	@PostMapping("/{userId}")
//	public ResponseEntity<String> findUserById(@PathVariable Long userId){ {
//	 	
//	 	return ResponseEntity.ok(userService.findUserById(userId));
//	  }
}
