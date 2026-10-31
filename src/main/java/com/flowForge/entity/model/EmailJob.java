package com.flowForge.entity.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(name = "job_id")
public class EmailJob extends Job{

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "send_to",
            joinColumns = @JoinColumn(name = "sendto_id")
    )
    private List<String> sendTo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "cc",
            joinColumns = @JoinColumn(name = "cc_id")
    )
    private List<String> cc;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "bcc",
            joinColumns = @JoinColumn(name = "bcc_id")
    )
    private List<String> bcc;
    private String subject;
    private String body;
}
