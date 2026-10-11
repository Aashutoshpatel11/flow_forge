package com.project.core.controller;

import com.project.core.entity.dto.RoleCreationRequestDto;
import com.project.core.entity.model.Role;
import com.project.core.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    public ResponseEntity<String> createRole(@Valid @RequestBody RoleCreationRequestDto roleDto){
        Role role = new Role();
        role.setName(roleDto.getRoleName().name());

        Role createdRole = roleService.createRole(role, roleDto.getSecretKey());

        if(createdRole==null){
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body("Wrong Secret Key");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("Role Created with name :: " + createdRole.getName());

    }

}
