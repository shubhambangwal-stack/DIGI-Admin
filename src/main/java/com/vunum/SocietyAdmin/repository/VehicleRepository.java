package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Users;
import com.vunum.SocietyAdmin.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByUser(Users user);
}
