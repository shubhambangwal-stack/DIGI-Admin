package com.vunum.SocietyAdmin.repository;


import com.vunum.SocietyAdmin.entity.Building;
import com.vunum.SocietyAdmin.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    Optional<Users> findByPhoneNumber(String phoneNumber);

    Optional<Users> findBytoken(String token);

    List<Users> findBybuilding(Building building);

    void deleteByEmailAllIgnoreCase(String email);

}
