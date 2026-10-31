package com.flowForge.service;

import com.flowForge.entity.dto.jobDto.CreateJobResponseDto;
import com.flowForge.entity.model.EmailJob;
import com.flowForge.entity.model.Job;
import com.flowForge.entity.model.User;
import com.flowForge.enums.Status;
import com.flowForge.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {
    private final JobRepository jobRepository;
    private final AuthService authService;

    public CreateJobResponseDto createJob(Job abstractJob){

        EmailJob job = null;
        if( abstractJob instanceof EmailJob emailJob){
            job = (EmailJob) abstractJob;
        }else{
            throw new RuntimeException("Invalid Job");
        }


        CreateJobResponseDto resp = new CreateJobResponseDto();

        Job savedJob = null;
        try {
            User user = authService.getCurrentUser();
            job.setUser(user);
            savedJob = jobRepository.save(job);
        } catch (Exception e) {
            resp.getErrors().add(e.getMessage());
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

    public List<Job> fetchAllJobs(){
        try {
            return jobRepository.findAll();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Job getJobById(UUID uuid){
        try {
            return jobRepository.getById(uuid);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
