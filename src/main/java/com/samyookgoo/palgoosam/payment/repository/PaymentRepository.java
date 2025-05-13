package com.samyookgoo.palgoosam.payment.repository;

import com.samyookgoo.palgoosam.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByBuyer_Id(Long buyerId);
}
