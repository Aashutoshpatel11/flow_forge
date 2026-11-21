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
import org.springframework.kafka.annotation.KafkaListener;
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


    @KafkaListener(topics = "process-email-job", groupId = "process-email-job-group")
    public void consume(EmailJob job) throws InterruptedException {
        log.info("Email processing started for job : " + job.getId());

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

        Thread.sleep(3000);

        sendMail(job);
    }

    public void sendMail( EmailJob currentJob ) throws InterruptedException {

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("official@flow.forge.com")
                .to(currentJob.getSendTo())
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
        } catch (ResendException e) {

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
