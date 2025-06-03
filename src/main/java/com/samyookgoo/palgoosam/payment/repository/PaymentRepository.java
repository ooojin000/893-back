package com.samyookgoo.palgoosam.payment.repository;

import com.samyookgoo.palgoosam.payment.constant.PaymentStatus;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.domain.PaymentForMyPageProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByAuction_Id(Long auctionId);

    Optional<Payment> findByOrderNumber(String orderNumber);

    Optional<Payment> findByAuctionIdAndStatus(Long auctionId, PaymentStatus status);

    Optional<Payment> findByAuctionId(Long auctionId);

    boolean existsByAuctionIdAndStatusIn(Long auctionId, List<PaymentStatus> statuses);

    boolean existsByAuction_IdAndStatus(Long auctionId, PaymentStatus status);

    @Query(value = """
            SELECT a.id as auctionId, p.order_number as orderNumber, ai.url as mainImageUrl, p.final_price as finalPrice, a.title as title
            FROM payment as p
            LEFT JOIN auction as a ON p.auction_id = a.id
            LEFT JOIN auction_image as ai ON a.id = ai.auction_id AND image_seq = 0
            WHERE p.buyer_id = :buyerId
            """, nativeQuery = true)
    List<PaymentForMyPageProjection> findAllPaymentForMyPageProjectionByBuyerId(@Param("buyerId") Long buyerId);
}
