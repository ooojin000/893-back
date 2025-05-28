package com.samyookgoo.palgoosam.payment.repository;

import com.samyookgoo.palgoosam.payment.constant.PaymentStatus;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByBuyer_Id(Long buyerId);

    Optional<Payment> findByAuction_Id(Long auctionId);

    Optional<Payment> findByOrderNumber(String orderNumber);

    Optional<Payment> findByAuctionIdAndStatus(Long auctionId, PaymentStatus status);

    Optional<Payment> findByAuctionId(Long auctionId);

    boolean existsByAuctionIdAndStatusIn(Long auctionId, List<PaymentStatus> statuses);

    boolean existsByAuction_IdAndStatus(Long auctionId, PaymentStatus status);
}
