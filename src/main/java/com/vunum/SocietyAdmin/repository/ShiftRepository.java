package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Admin;
import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    List<Shift> findByType(String type);

    Collection<? extends Shift> findByIssuer(Admin issuer);
}
