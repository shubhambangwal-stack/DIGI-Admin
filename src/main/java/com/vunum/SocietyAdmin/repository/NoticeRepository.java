package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByBuilding(Building building);
}
