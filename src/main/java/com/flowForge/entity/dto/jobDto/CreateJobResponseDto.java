package com.flowForge.entity.dto.jobDto;

import com.flowForge.entity.dto.BaseResponseDto;
import com.flowForge.enums.JobStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
public class CreateJobResponseDto extends BaseResponseDto {
    private UUID jobId;
    private JobStatus status;
}
