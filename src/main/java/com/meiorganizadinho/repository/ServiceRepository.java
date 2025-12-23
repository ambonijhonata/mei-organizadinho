package com.meiorganizadinho.repository;

import com.meiorganizadinho.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Services, Long> {
    boolean existsByNameIgnoreCase(String name);
    List<Services> findAllByOrderByNameAsc();
    List<Services> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
