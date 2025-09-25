package com.example.web_demo.repository;

import org.springframework.stereotype.Repository;
import com.example.web_demo.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}