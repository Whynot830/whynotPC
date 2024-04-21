package com.example.whynotpc.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for sending emails.
 */
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    String username;
    String subject = "Your order in WHYNOTPC shop";

    /**
     * Asynchronously sends an email with the provided body to the specified recipient.
     *
     * @param receiver The email address of the recipient
     * @param body     The body of the email
     * @throws MessagingException If an error occurs while sending the email
     */
    @Async
    public void sendMail(String receiver, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(username);
        helper.setSubject(subject);
        helper.setTo(receiver);
        helper.setText(body, true);

        javaMailSender.send(message);
    }
}
