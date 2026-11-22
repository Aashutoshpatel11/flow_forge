package com.flowForge.service;

import com.flowForge.entity.model.EmailJob;
import com.flowForge.enums.JobStatus;
import com.flowForge.enums.Status;
import com.flowForge.utils.EmailRenderer;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailProcessingService {

    private static final Logger log = LoggerFactory.getLogger(EmailProcessingService.class);
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final JavaMailSender mailSender;
    private final JobService jobService;
    private final EmailRenderer emailRenderer;

    @RetryableTopic(
            attempts = "3",
            backOff = @BackOff(
                    delay = 5000,
                    multiplier = 3.0,
                    maxDelay = 45000
            ),
            dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = "process-email-job", groupId = "process-email-job-group")
    public void consume(EmailJob job) throws InterruptedException {
        log.info("Received email job: {}", job.getId());

        job = (EmailJob) jobService.getJobById(job.getId());
        if( job.getStatus().equals(JobStatus.FAILED)
                || job.getStatus().equals(JobStatus.CANCELLED)
                || job.getStatus().equals(JobStatus.SUCCEEDED) ){
            return;
        }

        job.setStartedAt(LocalDateTime.now());
        job.setStatus(JobStatus.PROCESSING);
        job.setAttemptCount(job.getAttemptCount()+1);
        job = ( EmailJob ) jobService.updateJob(job);

        try{
            sendMail(job);
            job.setCompletedAt(LocalDateTime.now());
            job.setResult(Status.SUCCESS);
            job.setStatus(JobStatus.SUCCEEDED);
            job = (EmailJob) jobService.updateJob(job);

            log.info("Email job completed successfully: {}", job.getId());
        } catch (MessagingException e) {
            log.error("Email delivery failed for job {}: {}", job.getId(), e.getMessage());
            job.setResult(Status.FAILURE);

            if (job.getAttemptCount() >= job.getMaxCount()) {
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage("Max attempts reached: " + e.getMessage());
                jobService.updateJob(job);
            } else {
                job.setStatus(JobStatus.RETRYING);
                job.setErrorMessage(e.getMessage());
                jobService.updateJob(job);
                throw new RuntimeException("Triggering Kafka retry", e);
            }
        }   catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

//    public void sendMail( EmailJob currentJob ) throws ResendException {
//
//        CreateEmailOptions params = CreateEmailOptions.builder()
//                .from("onboarding@resend.dev")
//                .to("resend.dev")
//                .cc(currentJob.getCc())
//                .bcc(currentJob.getBcc())
//                .subject(currentJob.getSubject())
//                .html(emailRenderer.renderWrapper(currentJob.getSubject(), currentJob.getBody()))
//                .build();
//
//        resend.emails().send(params);
//
//    }

    public void sendMail( EmailJob currentJob ) throws MessagingException  {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(currentJob.getSendTo().toArray(new String[0]));

        if (currentJob.getCc() != null && !currentJob.getCc().isEmpty()) {
            helper.setCc(currentJob.getCc().toArray(new String[0]));
        }
        if (currentJob.getBcc() != null && !currentJob.getBcc().isEmpty()) {
            helper.setBcc(currentJob.getBcc().toArray(new String[0]));
        }

        helper.setSubject(currentJob.getSubject());
        helper.setText(emailRenderer.renderWrapper(currentJob.getSubject(), currentJob.getBody()), true);

        mailSender.send(message);

    }
}