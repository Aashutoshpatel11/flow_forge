package com.flowForge.service;

import com.flowForge.entity.model.EmailJob;
import com.flowForge.enums.JobStatus;
import com.flowForge.enums.Status;
import com.flowForge.utils.EmailRenderer;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.pl.REGON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailProcessingService {

    private static final Logger log = LoggerFactory.getLogger(EmailProcessingService.class);

    @Value("${resend.mail.api.key}")
    private static String resend_api_key;

    private final Resend resend = new Resend(resend_api_key);

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

        sendMail(job);
    }

    public void sendMail( EmailJob currentJob ) throws InterruptedException {

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("onboarding@resend.dev")
                .to("resend.dev")
                .cc(currentJob.getCc())
                .bcc(currentJob.getBcc())
                .subject(currentJob.getSubject())
                .html(emailRenderer.renderWrapper(currentJob.getSubject(), currentJob.getBody()))
                .build();

        try {
            resend.emails().send(params);

            currentJob.setCompletedAt(LocalDateTime.now());
            currentJob.setResult(Status.SUCCESS);
            currentJob.setStatus(JobStatus.SUCCEEDED);
            currentJob = (EmailJob) jobService.updateJob(currentJob);

            log.info("Email job completed successfully: {}", currentJob.getId());
        } catch (ResendException e) {

            log.error("Email delivery failed for job {}: {}", currentJob.getId(), e.getMessage());

            currentJob.setResult(Status.FAILURE);

            if( currentJob.getAttemptCount() >= currentJob.getMaxCount() ){
                currentJob.setStatus(JobStatus.FAILED);
                currentJob.setErrorMessage("max attempt reached, Failure :  " + e.getMessage());
                jobService.updateJob(currentJob);
            }else{
                currentJob.setStatus(JobStatus.RETRYING);
                currentJob.setErrorMessage(e.getMessage());
                jobService.updateJob(currentJob);
                consume((EmailJob) jobService.updateJob(currentJob));

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}