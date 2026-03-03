package com.vunum.SocietyAdmin.repository;

import com.vunum.SocietyAdmin.entity.Bill;
import com.vunum.SocietyAdmin.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByUserAndBillingMonth(Users user, String billingMonth);
}
