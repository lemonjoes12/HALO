package com.ptc.halo.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetOtp(
            String recipientEmail,
            String otp) {

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(recipientEmail);
        message.setSubject("HALO Password Reset Code");

        message.setText(
                "Your HALO password reset code is: "
                        + otp
                        + "\n\nThis code will expire soon."
                        + "\n\nIf you did not request a password reset, "
                        + "you can ignore this email."
        );

        mailSender.send(message);
    }
}