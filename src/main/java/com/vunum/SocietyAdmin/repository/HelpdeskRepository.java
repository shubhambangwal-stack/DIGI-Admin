package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Helpdesk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HelpdeskRepository extends JpaRepository<Helpdesk, Long> {
    List<Helpdesk> findByUserId(Long userId);
}