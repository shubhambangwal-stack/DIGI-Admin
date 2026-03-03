package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Admin;
import com.vunum.SocietyAdmin.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByPhoneNumber(String phoneNumber);

    List<Employee> findBySyndic(Admin syndic);
}
