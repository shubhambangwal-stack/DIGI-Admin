package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    long countByPollIdAndSelectedOption(Long pollId, String selectedOption);
    boolean existsByPollIdAndUserId(Long pollId, Long userId);
}
