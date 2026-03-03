package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    List<Consumption> findByBillingMonthAndBuilding(String billingMonth, Building building);

    List<Consumption> findByBuilding(Building building);
}
