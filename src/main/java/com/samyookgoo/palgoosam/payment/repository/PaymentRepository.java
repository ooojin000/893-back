package com.samyookgoo.palgoosam.payment.repository;

import com.samyookgoo.palgoosam.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
