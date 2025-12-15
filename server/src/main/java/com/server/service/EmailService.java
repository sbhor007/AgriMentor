package com.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    @Async("emailExecutor")
    public CompletableFuture<Boolean> sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send email to {}", to, e);
            return CompletableFuture.completedFuture(true);
        }
    }

    @Async("emailExecutor")
    public CompletableFuture<Boolean> sendEmail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("sbhor372@gmail.com");
            message.setSubject("Test Email");
            message.setText("This is a test email from the AuthController.");
            mailSender.send(message);
            log.info("Email sent successfully to ");
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send email to ", e);
            return CompletableFuture.completedFuture(true);
        }
    }

    @Async("emailExecutor")
    public CompletableFuture<Boolean> sendEmail(String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("sbhor372@gmail.com");
            message.setSubject("Admin Notification");
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to ");
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send email to ", e);
            return CompletableFuture.completedFuture(true);
        }
    }

    @Async("emailExecutor")
    public CompletableFuture<Boolean> sendEmail(String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("sbhor372@gmail.com");
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to {}", "");
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed to send email to {}", "", e);
            return CompletableFuture.completedFuture(true);
        }
    }
}
