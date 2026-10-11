package com.project.core.repository;

import com.project.core.entity.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @EntityGraph(attributePaths = {"roles"})
    public User getUserByEmail(String email);

}
