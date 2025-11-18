package com.meiorganizadinho.repository;

import com.meiorganizadinho.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    boolean existsByName(String name);
}
