package com.meiorganizadinho.repository;

import com.meiorganizadinho.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    boolean existsByName(String name);
    List<Client> findAllByOrderByNameAsc();
    List<Client> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
}
