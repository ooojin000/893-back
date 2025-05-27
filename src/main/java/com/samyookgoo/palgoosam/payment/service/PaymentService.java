package com.samyookgoo.palgoosam.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.config.TossPaymentsConfig;
import com.samyookgoo.palgoosam.payment.constant.PaymentStatus;
import com.samyookgoo.palgoosam.payment.controller.request.PaymentCreateRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentConfirmRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentFailCallbackRequest;
import com.samyookgoo.palgoosam.payment.controller.response.PaymentCreateResponse;
import com.samyookgoo.palgoosam.payment.controller.response.TossPaymentConfirmResponse;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.policy.DeliveryPolicy;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final RestTemplate tossRestTemplate;
    private final TossPaymentsConfig config;
    private final PaymentRepository paymentRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final ObjectMapper objectMapper;
    private final DeliveryPolicy deliveryPolicy;


    @Transactional
    public PaymentCreateResponse createPayment(Long auctionId, User buyer, PaymentCreateRequest request) {
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

        if (paymentRepository.existsByAuctionIdAndStatusIn(auctionId,
                List.of(PaymentStatus.PAID, PaymentStatus.PENDING))) {
            throw new IllegalStateException("이미 결제 중이거나 완료된 주문입니다.");
        }

        if (!request.getItemPrice().equals(winningBid.getPrice())) {
            throw new IllegalArgumentException("결제 금액이 낙찰 금액과 일치하지 않습니다.");
        }

        int deliveryFee = deliveryPolicy.calculate(request.getItemPrice());
        if (!request.getDeliveryFee().equals(deliveryFee)) {
            throw new IllegalArgumentException("배송비가 배송비가 올바르지 않습니다.");
        }

        Payment payment = paymentRepository.findByAuction_Id(auctionId)
                .orElseGet(() -> {
                    String orderNumber = generateOrderNumber(auctionId);
                    Payment newPayment = Payment.builder()
                            .buyer(buyer)
                            .seller(auction.getSeller())
                            .auction(auction)
                            .recipientName(request.getRecipientName())
                            .recipientEmail(buyer.getEmail())
                            .phoneNumber(request.getPhoneNumber())
                            .addressLine1(request.getAddressLine1())
                            .addressLine2(request.getAddressLine2())
                            .zipCode(request.getZipCode())
                            .itemPrice(request.getItemPrice())
                            .deliveryFee(request.getDeliveryFee())
                            .finalPrice(request.getItemPrice() + request.getDeliveryFee())
                            .orderNumber(orderNumber)
                            .status(PaymentStatus.READY)
                            .build();
                    return paymentRepository.save(newPayment);
                });

        return PaymentCreateResponse.builder()
                .orderId(payment.getOrderNumber())
                .orderName(auction.getTitle())
                .successUrl(request.getSuccessUrl())
                .failUrl(request.getFailUrl())
                .customerEmail(payment.getRecipientEmail())
                .customerName(payment.getRecipientName())
                .customerMobilePhone(payment.getPhoneNumber())
                .finalPrice(payment.getFinalPrice())
                .build();
    }

    public void handlePaymentFailure(TossPaymentFailCallbackRequest request) {
        Payment payment = paymentRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(() -> new NoSuchElementException("해당 경매를 찾을 수 없습니다."));

        if (payment.getStatus() == PaymentStatus.READY) {
            payment.setStatus(PaymentStatus.FAILED);
        }
    }

    @Transactional
    public TossPaymentConfirmResponse confirmPayment(TossPaymentConfirmRequest request) {
        Payment payment = paymentRepository.findByOrderNumber(request.getOrderId())
                .orElseThrow(() -> new NoSuchElementException("해당 주문을 찾을 수 없습니다."));

        if (payment.getStatus().equals(PaymentStatus.PAID)) {
            throw new IllegalStateException("이미 결제 처리된 주문입니다.");
        }

        if (payment.getFinalPrice() != request.getAmount()) {
            throw new IllegalArgumentException("결제 금액이 일치하지 않습니다.");
        }

        String url = config.getBaseUrl() + "/v1/payments/confirm";

        payment.setType(request.getPaymentType());
        payment.setPaymentKey(request.getPaymentKey());

        try {
            @SuppressWarnings("unchecked")
            TossPaymentConfirmResponse tossResponse = tossRestTemplate.postForObject(url, request,
                    TossPaymentConfirmResponse.class);

            if (tossResponse == null) {
                payment.setStatus(PaymentStatus.FAILED);
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "토스 응답이 비어 있습니다.");
            }

            payment.setStatus(PaymentStatus.PAID);
            payment.setApprovedAt(OffsetDateTime.parse(tossResponse.getApprovedAt()).toLocalDateTime());

            tossResponse.setCustomerEmail(payment.getRecipientEmail());
            tossResponse.setCustomerName(payment.getRecipientName());
            tossResponse.setCustomerMobilePhone(payment.getPhoneNumber());
            return tossResponse;

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            payment.setStatus(PaymentStatus.FAILED);

            String responseBody = ex.getResponseBodyAsString();
            String code = "UNKNOWN";
            String message = "Toss 결제 오류가 발생했습니다.";

            try {
                Map<String, Object> errorMap = objectMapper.readValue(responseBody, Map.class);
                code = errorMap.getOrDefault("code", code).toString();
                message = errorMap.getOrDefault("message", message).toString();
            } catch (Exception parseEx) {
                log.warn("Toss 결제 오류 응답 파싱 실패: {}", parseEx.getMessage());
            }

            log.error("Toss 결제 승인 실패 - code: {}, message: {}", code, message);
            throw new ResponseStatusException(ex.getStatusCode(), "[" + code + "] " + message);

        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
            log.error("Toss 결제 처리 중 예외 발생", ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Toss 결제 오류가 발생했습니다.");
        }
    }

    private String generateOrderNumber(Long auctionId) {
        return String.format("ORD-%d-%s", auctionId, UUID.randomUUID().toString().substring(0, 8));
    }

}
