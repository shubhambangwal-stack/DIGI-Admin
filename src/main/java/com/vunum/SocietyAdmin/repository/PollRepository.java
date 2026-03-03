package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PollRepository extends JpaRepository<Poll, Long> {
    List<Poll> findByStatus(Poll.Status status);

    List<Poll> findByEndDateBefore(LocalDate now);
}
