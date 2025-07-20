package com.example.csvprocessor;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JavaMailSender mailSender;
    private final String toEmail;
    private final String fromEmail;

    @Autowired
    public JobCompletionNotificationListener(JavaMailSender mailSender,
                                           @Value("${notification.email.to}") String toEmail,
                                           @Value("${notification.email.from}") String fromEmail) {
        this.mailSender = mailSender;
        this.toEmail = toEmail;
        this.fromEmail = fromEmail;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("CSV Processing Job Failed");
            message.setText("The CSV processing job has failed with the following exception: \n" +
                    jobExecution.getAllFailureExceptions());
            mailSender.send(message);
        }
    }
}
