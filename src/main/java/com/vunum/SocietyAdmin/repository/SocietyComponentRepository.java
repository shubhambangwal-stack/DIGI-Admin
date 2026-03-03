package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.SocietyComponent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SocietyComponentRepository extends JpaRepository<SocietyComponent, Long> {
    List<SocietyComponent> findByType(String type);
}