package com.project.core.repository;

import com.project.core.entity.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    public Role getRoleByName(String role);

}
