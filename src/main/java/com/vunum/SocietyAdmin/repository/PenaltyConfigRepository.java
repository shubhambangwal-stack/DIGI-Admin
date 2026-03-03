package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.PenaltyConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PenaltyConfigRepository extends JpaRepository<PenaltyConfig, Long> {

    Optional<PenaltyConfig> findByPenaltyType(String penaltyType);
}
