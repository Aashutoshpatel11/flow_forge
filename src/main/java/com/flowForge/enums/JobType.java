package com.flowForge.enums;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum JobType {
    EMAIL("EMAIL");

    private final String value;
}
