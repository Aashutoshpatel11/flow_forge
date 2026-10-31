package com.flowForge.controller;

import com.flowForge.entity.dto.BaseResponseDto;
import com.flowForge.entity.dto.jobDto.CreateJobRequestDto;
import com.flowForge.entity.dto.jobDto.CreateJobResponseDto;
import com.flowForge.entity.dto.jobDto.EmailJobDto;
import com.flowForge.entity.model.EmailJob;
import com.flowForge.entity.model.Job;
import com.flowForge.enums.JobType;
import com.flowForge.enums.Status;
import com.flowForge.service.JobService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/job")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final Validator validator;

    @PostMapping("/create")
    public ResponseEntity<CreateJobResponseDto> createJob(@Valid @RequestBody CreateJobRequestDto dto){

        switch (dto){
            case @Valid EmailJobDto emailJobDto -> {

                Set<ConstraintViolation<EmailJobDto>> violations = validator.validate(emailJobDto);

                if (!violations.isEmpty()) {
                    throw new ConstraintViolationException(violations);
                }

                EmailJob job = new EmailJob();
                job = new EmailJob();
                job.setType(JobType.EMAIL);
                job.setSendTo(emailJobDto.getSendTo());
                job.setCc(emailJobDto.getCc());
                job.setBcc(emailJobDto.getBcc());
                job.setSubject(emailJobDto.getSubject());
                job.setBody(emailJobDto.getBody());

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(jobService.createJob(job));
            }
            default -> {
                throw new RuntimeException("Required Job is not Serviceable");
            }
        }

    }

    @GetMapping
    public ResponseEntity<List<Job>> getALlJobs(){
        return ResponseEntity.ok().body(jobService.fetchAllJobs());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Job> getJobById(@PathVariable String uuid){
        return ResponseEntity.ok().body( jobService.getJobById(UUID.fromString(uuid)) );
    }

    @PatchMapping("/cancel/{uuid}")
    public ResponseEntity<BaseResponseDto> cancelJob(@PathVariable String uuid){
        jobService.cancelJob(UUID.fromString(uuid));
        return ResponseEntity.ok()
                .body(new BaseResponseDto(Status.SUCCESS, "Job cancelled Successfully", ""));
    }

}
