package com.example.lab4.db;

import com.example.lab4.models.Point;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepositoryJPA extends JpaRepository<Point, Long> {
}
