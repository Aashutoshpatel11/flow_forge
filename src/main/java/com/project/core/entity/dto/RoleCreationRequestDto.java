package com.project.core.entity.dto;

import com.project.core.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RoleCreationRequestDto {
    private Role roleName;
    private String secretKey;
}
