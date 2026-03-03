package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingManagementRepository extends JpaRepository<Building, Long> {
    Building findByBuildingNumber(Long buildingNumber);
    

    @Query("SELECT b FROM Building b WHERE LOWER(b.buildingName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Building> searchByBuildingName(@Param("keyword") String keyword);

    List<Building> findBysyndicName(String syndicName);
}
