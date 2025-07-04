package com.hospital.Hospital_Management_System.service.MailService;



import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

// Interface defining the methods for sending mail
public interface IMailService {

    // Method to send a simple email
    String sendMail(MailDetailsDto mailDetailsDto);

    String sendHtmlMail(MailDetailsDto mailDetailsDto);

    // Method to send an email with attachment
//    String sendMailWithAttachment(MailDetailsDto mailDetailsDto);
}


