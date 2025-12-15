package com.server.controller;

import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;

import com.server.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.server.dto.CunsultantRegisterRequest;
import com.server.dto.FarmerRegistrationRequest;
import com.server.dto.LoginRequest;
import com.server.dto.RegisterRequest;
import com.server.dto.ResetPasswordRequest;
import com.server.dto.SendOtpRequest;
import com.server.dto.VerifyOtpRequest;
import com.server.entity.User;
import com.server.enumeration.Role;
import com.server.repository.EmailOtpRepository;
import com.server.repository.UserRepository;
import com.server.response.ApiResponse;
import com.server.service.AuthService;
import com.server.service.OtpService;
import com.server.service.UserService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/auth")
//@CrossOrigin(origins ="*")
@Slf4j
public class AuthController{

	@Autowired
    private  EmailOtpRepository emailOtpRepository;
	
	@Autowired
	private  UserRepository userRepository;
	
	@Autowired
    private  UserService userService;
	@Autowired
	private AuthService authService;
	 @Autowired
	    private OtpService otpService;

     @Autowired
     private EmailService emailService;

	 @PostMapping("/register")
	    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
	        log.info("Received user registration request: {}", request);

	        try {
	            User user = authService.register(request);
	            return ResponseEntity.ok(user);   // Or return a DTO (recommended)
	        } catch (Exception e) {
	            log.error("User registration failed", e);
	            return ResponseEntity
	                    .badRequest()
	                    .body("User registration failed: " + e.getMessage());
	        }
	    }

     @GetMapping("/isUserAlreadyExist")
     public ResponseEntity<ApiResponse<?>> isUserAlreadyExist(@RequestParam String username, @RequestParam Role role){
         log.info("inside isUserAlreadyExist");
         try {
             return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK,"",this.authService.isUserAlreadyExist(username,role)));
         }catch (Exception e){
             return null;
         }
     }

	@PostMapping("/register/farmer")
	public ResponseEntity<?> registerFarmer(@RequestBody FarmerRegistrationRequest request) {
	    log.info("Registration Request Received: {}", request);

	    if (request.getRole() != Role.FARMER) {
	        throw new IllegalArgumentException("Invalid role for farmer registration");
	    }

	    return ResponseEntity.ok().body(
	        new ApiResponse<>(HttpStatus.OK, "User registered successfully",
	                authService.registerFarmer(request).orElseThrow(() -> new RuntimeException("Registration failed"))
	                )
	    );
	}

	@PostMapping(value = "/register/consultant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> registerConsultant(@ModelAttribute CunsultantRegisterRequest request) {
		log.info("Registering Consultant: {}", request);
		try {
			return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK, "User registered successfully",
					authService.registerConsultant(request)));
		} catch (Exception e) {
			return ResponseEntity.status(400).body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, e.getMessage()));
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
		try {
			log.info("Login attempt for user: {}", loginRequest.toString());
			return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK, "User Login successfully",
					authService.login(loginRequest)));
		} catch (Exception e) {
			log.error("Login failed for user: {}", e.getMessage());
			return ResponseEntity.status(400).body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, e.getMessage()));
		}
	}
	
	
	@PostMapping("/send-otp/{email}")
    public ResponseEntity<?> sendOtp(@PathVariable String email){
        
        try {
        	 otpService.sendOtp(email);
        	 OtpService.generateOtp(6);
             return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK,"OTP send succesfully"));
		} catch (Exception e) {
			log.error("Login failed for user: {}", e.getMessage());
			return ResponseEntity.status(400).body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, e.getMessage()));
		}

	}
	
	@PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequest req){
        boolean result = otpService.verifyOtp(req.getEmail(), req.getOtp());
        return result ? ResponseEntity.ok("Verified")
                      : ResponseEntity.badRequest().body("Invalid or expired OTP");
    }
	
	//....forgot password....//
	

    
    @PostMapping("/Forgot-password/{email}")
    public ResponseEntity<?> sendForgotPasswordOtp(@PathVariable String email){
        
        try {
        	log.error("in otp forgot for user: {}");
        	 otpService.sendOtp(email);
        	 otpService.generateOtp(6);
             return ResponseEntity.ok().body(new ApiResponse<>(HttpStatus.OK,"OTP send succesfully"));
		} catch (Exception e) {
			log.error("password change failed for user: {}", e.getMessage());
			return ResponseEntity.status(400).body(new ApiResponse<String>(HttpStatus.BAD_REQUEST, e.getMessage()));
		}

	}

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        boolean valid = otpService.verifyOtp(request.getEmail(), request.getOtp());

        if (!valid) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }

        userService.updatePassword(request.getEmail(), request.getNewPassword());
      
      

        // 🔥 DELETE OTP AFTER SUCCESS
        emailOtpRepository.deleteByEmail(request.getEmail());

        return ResponseEntity.ok("Password reset successful");
        
    }

    @PostMapping("/test/mail")
    public ResponseEntity<?> testEmailSending() {
        try {
//            CompletableFuture<Boolean> result = emailService.sendEmail(
//                    "sbhor747@gmail.com",
//                    "Test Email",
//                    "This is a test email from the AuthController."
//            );

            // If you want NON-BLOCKING response:
            // TODO:change letter email send mail paramiters
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK,
                    "Email task started in background. Check logs.",
                    emailService.sendEmail()
                    ));
        } catch (Exception e) {
            log.error("Test email sending failed", e);
            return ResponseEntity.status(500).body(new ApiResponse<String>(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }
	

}
