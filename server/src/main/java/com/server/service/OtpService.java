package com.server.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.server.entity.EmailOtp;
import com.server.repository.EmailOtpRepository;
import com.server.util.OtpUtil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import com.server.repository.EmailOtpRepository;

@Service
@Slf4j
public class OtpService {
	 @Autowired
     private JavaMailSender mailSender;
      @Autowired
      private PasswordEncoder passwordEncoder;
	    @Autowired
	    private EmailOtpRepository repo;

	    private final int EXPIRY_MIN = 5;

	    @Transactional
//        @Async
	    public void sendOtp(String email){

	        
	        String otp = OtpUtil.generateOtp(6);
	        LocalDateTime expiry = LocalDateTime.now().plusMinutes(EXPIRY_MIN);

	        // delete old otp if exists
	        repo.deleteByEmail(email);

	        // save new otp in db
	        EmailOtp entity = new EmailOtp();
	        entity.setEmail(email);
	        entity.setOtp(otp);
	        entity.setExpiryAt(expiry);
	        entity.setVerified(false);
	        repo.save(entity);

	        // send email
	        SimpleMailMessage msg = new SimpleMailMessage();
	        msg.setTo(email);
	        msg.setSubject("Your OTP Code");
	        msg.setText("Your OTP is: " + otp + "\nValid for " + EXPIRY_MIN + " minutes.");

	        mailSender.send(msg);
	    }

        @Transactional
	    public boolean verifyOtp(String email, String otp){

	        Optional<EmailOtp> e = repo.findTopByEmailOrderByCreatedAtDesc(email);

	        if (e.isEmpty()) return false;

	        EmailOtp data = e.get();
	        log.info(" otp received...");
	        // check expiry
	        if (data.getExpiryAt().isBefore(LocalDateTime.now())) {
	            return false;
	        }

	        // check match
	        log.info("DB OTP : {}",data.getOtp());
	        log.info("Encoded  OTP : {}",otp);
	        log.info("check isOTPValid : {}",data.getOtp().equals(otp));
	        if (!data.getOtp().equals(otp)) {
	        	log.info("in otp match block");
	            return false;
	        }

	        // mark verified
	        data.setVerified(true);
	        repo.save(data);
	        return true;
	    }
        

   	 private static final SecureRandom random = new SecureRandom();

   	    public static String generateOtp(int digits){
   	        int bound = (int) Math.pow(10, digits);
   	        int num = random.nextInt(bound - bound/10) + bound/10;
   	        return String.valueOf(num);
   	    }

}
