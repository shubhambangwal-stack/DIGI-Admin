package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.ResidentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ResidentActivityRepository extends JpaRepository<ResidentActivity, Long> {
    List<ResidentActivity> findByresidentId(Long residentId);
}
