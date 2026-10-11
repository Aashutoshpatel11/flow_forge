package com.project.core.service;

import com.project.core.entity.model.Role;
import com.project.core.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Value("${role.creation.key}")
    private String roleCreationKey;

    public Role getRoleByName(com.project.core.enums.Role role){
        return roleRepository.getRoleByName(role.toString());
    }

    public Role createRole(Role role, String receivedKey){
        if( !roleCreationKey.equals(receivedKey) ){
            return null;
        }
        return roleRepository.save(role);
    }

}
