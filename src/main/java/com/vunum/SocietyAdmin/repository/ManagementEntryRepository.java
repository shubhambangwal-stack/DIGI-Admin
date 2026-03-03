package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.ManagementEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagementEntryRepository extends JpaRepository<ManagementEntry, Long> {
    List<ManagementEntry> findAllByTypeAndBuildingId(String type, Long BuildingId);
}
