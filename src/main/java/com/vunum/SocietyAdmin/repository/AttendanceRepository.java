package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
