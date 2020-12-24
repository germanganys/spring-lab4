package com.example.lab4.jpa;

import com.example.lab4.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositoryJPA extends JpaRepository<User, Long> {
}
