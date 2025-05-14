package com.samyookgoo.palgoosam.payment.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.payment.controller.request.CreatePaymentRequest;
import com.samyookgoo.palgoosam.payment.controller.response.PaymentResponse;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.domain.PaymentStatus;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;

    public PaymentResponse createPayment(Long auctionId, User buyer, CreatePaymentRequest request) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("해당 경매를 찾을 수 없습니다."));

        if (auction.getSeller().getId().equals(buyer.getId())) {
            throw new IllegalArgumentException("판매자는 자신의 경매를 구매할 수 없습니다.");
        }

        Bid winningBid = bidRepository.findByAuctionIdAndIsWinningTrue(auctionId)
                .orElseThrow(() -> new IllegalStateException("낙찰된 입찰이 존재하지 않습니다."));

        if (!winningBid.getBidder().getId().equals(buyer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "낙찰자만 결제할 수 있습니다.");
        }

        if (!request.getFinalPrice().equals(winningBid.getPrice())) {
            throw new IllegalArgumentException("결제 금액이 낙찰 금액과 일치하지 않습니다.");
        }

        String orderNumber = generateOrderNumber(auctionId);
        Payment payment = Payment.builder()
                .buyer(buyer)
                .seller(auction.getSeller())
                .auction(auction)
                .recipientName(request.getRecipientName())
                .phoneNumber(request.getPhoneNumber())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .zipCode(request.getZipCode())
                .finalPrice(winningBid.getPrice())
                .orderNumber(orderNumber)
                .status(PaymentStatus.READY)
                .method(request.getPaymentMethod())
                .build();

        paymentRepository.save(payment);

        return PaymentResponse.builder()
                .orderId(orderNumber)
                .orderName(auction.getTitle())
                .successUrl(request.getSuccessUrl())
                .failUrl(request.getFailUrl())
                .customerEmail(buyer.getEmail())
                .customerName(buyer.getName())
                .customerMobilePhone(request.getPhoneNumber())
                .finalPrice(request.getFinalPrice())
                .build();
    }

    private String generateOrderNumber(Long auctionId) {
        return String.format("ORD-%d-%s", auctionId, UUID.randomUUID().toString().substring(0, 8));
    }

}
