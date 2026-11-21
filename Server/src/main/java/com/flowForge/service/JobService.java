package com.flowForge.service;

import com.flowForge.entity.dto.jobDto.CreateJobResponseDto;
import com.flowForge.entity.model.EmailJob;
import com.flowForge.entity.model.Job;
import com.flowForge.entity.model.User;
import com.flowForge.enums.JobStatus;
import com.flowForge.enums.Status;
import com.flowForge.exception.FlowForgeException;
import com.flowForge.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {
    private static final Logger log = LoggerFactory.getLogger(JobService.class);
    private final JobRepository jobRepository;
    private final AuthService authService;
    private final KafkaTemplate<String, EmailJob> kafkaTemplate;

    public CreateJobResponseDto createJob(Job abstractJob){
        CreateJobResponseDto resp = new CreateJobResponseDto();

        EmailJob job = null;
        EmailJob savedJob = null;

        if( abstractJob instanceof EmailJob emailJob){
            job = (EmailJob) abstractJob;
            try {
                User user = authService.getCurrentUser();
                job.setUser(user);
                savedJob = (EmailJob) jobRepository.save(job);
                log.info("Posting Job to kafka with ID : " + savedJob.getId());
                kafkaTemplate.send("process-email-job", savedJob.getId().toString(), savedJob);
            } catch (Exception e) {
                resp.getErrors().add(e.getMessage());
            }
        }else{
            throw new RuntimeException("Invalid Job");
        }

        if (resp.getErrors().isEmpty() && savedJob != null) {
            resp.setJobId(savedJob.getId());
            resp.setStatus(savedJob.getStatus());
            resp.setResponseStatus(Status.SUCCESS);
            resp.setMessage("Job Created Successfully");
        } else {
            resp.setResponseStatus(Status.FAILURE);
            resp.setMessage("Job Creation Failed");
        }

        return resp;
    }

    public List<Job> fetchUserAllJobs(){

        try {
            UUID userId = authService.getCurrentUser().getId();
            return jobRepository.findByUserId(userId);
        } catch (RuntimeException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Job getJobById(UUID uuid){
        try {
            return jobRepository.getJobById(uuid);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean cancelJob(UUID uuid){
        try {
            Job job = getJobById(uuid);

            if( !job.getUser().getEmail().equals(authService.getCurrentUser().getEmail()) ){
                throw new FlowForgeException("User does not own the job with id : " + uuid.toString());
            }

            if( job.getStatus().equals(JobStatus.SUCCEEDED)
                    || job.getStatus().equals(JobStatus.PROCESSING)
                    || job.getStatus().equals(JobStatus.CANCELLED)
                    || job.getStatus().equals(JobStatus.RETRYING)
            ){
                throw new FlowForgeException("Job is already completed in : " + job.getStatus().toString().toUpperCase() + "status");
            }

            job.setStatus(JobStatus.CANCELLED);
            jobRepository.save(job);
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error canceling job : " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Job updateJob(Job job){
        try {

            return jobRepository.save(job);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
