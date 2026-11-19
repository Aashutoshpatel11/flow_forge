package com.flowForge.entity.dto.jobDto;

import com.flowForge.enums.JobType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class EmailJobDto extends CreateJobRequestDto {

    @NotEmpty(message = "Send to field can't be empty")
    private List< @Email(message = "Provide a valid email address in send to field") String> sendTo;

    private List< @Email(message = "Provide a valid email address in cc field") String> cc;

    private List< @Email(message = "Provide a valid email address in bcc field") String> bcc;

    @NotEmpty(message = "subject field can't be empty")
    private String subject;

    @NotEmpty(message = "body field can't be empty")
    private String body;


}
