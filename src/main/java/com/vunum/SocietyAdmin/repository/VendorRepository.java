package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    List<Vendor> findByBuilding(Building building);
}
