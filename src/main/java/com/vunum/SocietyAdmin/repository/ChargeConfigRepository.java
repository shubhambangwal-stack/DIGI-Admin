package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.ChargeConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChargeConfigRepository extends JpaRepository<ChargeConfig, Long> {
    Optional<List<ChargeConfig>> findBybuildingAndResidentTypeAndFlatType(Building building, String residentType, String flatType);

    Optional<ChargeConfig> findBybuildingAndType(Building building, ChargeConfig.type type);

    List<ChargeConfig> findBybuilding(Building building);
}

