package com.flowForge.service;

import com.flowForge.entity.model.EmailJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailProcessingService {

    private static final Logger log = LoggerFactory.getLogger(EmailProcessingService.class);

    @KafkaListener(topics = "process-email-job", groupId = "process-email-job-group")
    public void consume(EmailJob job){

        log.info("Starting email job processing");
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Processing Email JOB :: ");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info(job.toString());
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("Email sent Successfully : " + job.getSendTo() );

    }

}
