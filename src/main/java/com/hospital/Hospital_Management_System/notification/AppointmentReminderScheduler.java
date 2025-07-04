package com.hospital.Hospital_Management_System.notification;

import com.hospital.Hospital_Management_System.dto.MailDetailsDto;
import com.hospital.Hospital_Management_System.entity.Appointment;
import com.hospital.Hospital_Management_System.repository.AppointmentRepository;
import com.hospital.Hospital_Management_System.service.MailService.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentReminderScheduler {

    private final AppointmentRepository appointmentRepository;
    private final MailService mailService;

    @Scheduled(cron = "0 0 10 * * ?")
    public void sendReminders() {
        LocalDate targetDate = LocalDate.now().plusDays(2);

        List<Appointment> appointments = appointmentRepository.findAll();

        for (Appointment appointment : appointments) {
            LocalDate appointmentDate = appointment.getDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            if (appointmentDate.equals(targetDate)) {

                String html = """
                    <html>
                    <head>
                      <style>
                        body {
                          font-family: Arial, sans-serif;
                          background-color: #f9f9f9;
                          padding: 20px;
                        }
                        .container {
                          background-color: #ffffff;
                          padding: 20px;
                          border-radius: 10px;
                          box-shadow: 0 0 10px rgba(0,0,0,0.1);
                          max-width: 600px;
                          margin: auto;
                        }
                        .header {
                          color: #e67e22;
                          font-size: 20px;
                          margin-bottom: 15px;
                        }
                        .content {
                          font-size: 16px;
                          color: #333333;
                        }
                        .footer {
                          margin-top: 20px;
                          font-size: 13px;
                          color: #888888;
                        }
                      </style>
                    </head>
                    <body>
                      <div class="container">
                        <div class="header">⏰ Appointment Reminder</div>
                        <div class="content">
                          <p>Dear %s,</p>
                          <p>This is a reminder that your appointment with <strong>Dr. %s</strong> is scheduled on <strong>%s</strong>.</p>
                          <p><strong>Please make sure to attend.</strong> If you don’t attend, you will need to book a new appointment.</p>
                        </div>
                        <div class="footer">
                          Thank you for choosing our hospital.
                        </div>
                      </div>
                    </body>
                    </html>
                    """.formatted(
                        appointment.getPatient().getName(),
                        appointment.getDoctor().getName(),
                        appointment.getDate()
                );

                mailService.sendMail(MailDetailsDto.builder()
                        .toMail(appointment.getPatient().getEmail())
                        .subject("⏰ Reminder: Your Appointment is in 2 Days")
                        .message(html)
                        .contentType("html")
                        .build());
            }
        }
    }
}
