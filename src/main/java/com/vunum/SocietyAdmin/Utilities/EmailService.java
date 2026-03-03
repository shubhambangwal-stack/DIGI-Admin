package com.vunum.SocietyAdmin.Utilities;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;

import static javax.print.attribute.standard.Severity.ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
@Service
public class EmailService {

    @Autowired
    private final JavaMailSender mailSender;

    @Async
    public void sendMail(String toEmail, String content, String subject, MultipartFile file)
            throws MessagingException, UnsupportedEncodingException {

        try {
            String senderName = "DIGI-IMMO";
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom("no-reply@digiimmo.in", senderName);
            messageHelper.setTo(toEmail);
            messageHelper.setSubject(subject);
            if (file != null) messageHelper.addAttachment("Invoice.pdf", file);
            messageHelper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.info(String.valueOf(e), ERROR);
            throw e;
        }
    }
}