package com.ramongibson.lockboxapi.repository;

import com.ramongibson.lockboxapi.model.Password;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    List<Password> findByUserUsername(String username);
}