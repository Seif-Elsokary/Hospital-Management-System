package com.hospital.Hospital_Management_System.service.MailService;

import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@RequiredArgsConstructor
@Service
public class MailService implements IMailService {

    private final JavaMailSender mailSender;

    @Override
    public String sendMail(MailDetailsDto mailDetailsDto) {
        try {
            if ("html".equalsIgnoreCase(mailDetailsDto.getContentType())) {
                return sendHtmlMail(mailDetailsDto);
            } else {
                return sendPlainMail(mailDetailsDto);
            }
        } catch (Exception e) {
            return "Failed to send email: " + e.getMessage();
        }
    }

    @Override
    public String sendHtmlMail(MailDetailsDto mailDetailsDto) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(mailDetailsDto.getToMail());
            helper.setSubject(mailDetailsDto.getSubject());
            helper.setText(mailDetailsDto.getMessage(), true);
            mailSender.send(mimeMessage);
            return "HTML email sent successfully to " + mailDetailsDto.getToMail();
        } catch (Exception e) {
            return "Failed to send HTML email: " + e.getMessage();
        }
    }

    private String sendPlainMail(MailDetailsDto mailDetailsDto) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailDetailsDto.getToMail());
            message.setSubject(mailDetailsDto.getSubject());
            message.setText(mailDetailsDto.getMessage());
            mailSender.send(message);
            return "Plain text email sent successfully to " + mailDetailsDto.getToMail();
        } catch (Exception e) {
            return "Failed to send plain text email: " + e.getMessage();
        }
    }

//    @Override
//    public String sendMailWithAttachment(MailDetailsDto mailDetailsDto) {
//        try {
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//            helper.setTo(mailDetailsDto.getToMail());
//            helper.setSubject(mailDetailsDto.getSubject());
//            helper.setText(mailDetailsDto.getMessage(), true);
//
//            File file = new File("C:\\Users\\HP\\Downloads\\springBoot.png");
//            if (file.exists()) {
//                helper.addAttachment(file.getName(), file);
//            } else {
//                return "Attachment file not found!";
//            }
//
//            mailSender.send(mimeMessage);
//            return "Email with attachment sent successfully to " + mailDetailsDto.getToMail();
//        } catch (Exception e) {
//            return "Failed to send email with attachment: " + e.getMessage();
//        }
//    }

}