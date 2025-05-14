package com.samyookgoo.palgoosam.payment.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.deliveryaddress.repository.DeliveryAddressRepository;
import com.samyookgoo.palgoosam.payment.controller.request.CreatePaymentRequest;
import com.samyookgoo.palgoosam.payment.controller.response.OrderResponse;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final AuctionImageRepository auctionImageRepository;

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

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long auctionId, Long userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("경매 상품이 존재하지 않습니다."));

        Bid winningBid = bidRepository.findByAuctionIdAndIsWinningTrue(auctionId)
                .orElseThrow(() -> new IllegalStateException("낙찰된 입찰이 존재하지 않습니다."));

        if (!winningBid.getBidder().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "낙찰자만 결제할 수 있습니다.");
        }

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findDefaultByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("기본 배송지가 없습니다."));

        String orderNumber = generateOrderNumber(auctionId);

        AuctionImage image = auctionImageRepository.findMainImageByAuctionId(auctionId)
                .orElseThrow(() -> new IllegalStateException("해당 경매에 대한 대표 이미지가 존재하지 않습니다."));

        return OrderResponse.builder()
                .orderId(orderNumber)
                .auctionId(auction.getId())
                .auctionTitle(auction.getTitle())
                .auctionThumbnail(image != null ? image.getUrl() : null)
                .finalPrice(winningBid.getPrice())
                .deliveryAddress(DeliveryAddressResponseDto.of(deliveryAddress))
                .paymentMethod(null) // 아직 선택되지 않음
                .paymentStatus(PaymentStatus.READY)
                .build();
    }

    private String generateOrderNumber(Long auctionId) {
        return String.format("ORD-%d-%s", auctionId, UUID.randomUUID().toString().substring(0, 8));
    }

}
