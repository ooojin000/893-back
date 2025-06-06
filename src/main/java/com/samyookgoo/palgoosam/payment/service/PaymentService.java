package com.samyookgoo.palgoosam.payment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.exception.AuctionNotFoundException;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.exception.BidNotFoundException;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.config.TossPaymentsConfig;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.payment.constant.PaymentStatus;
import com.samyookgoo.palgoosam.payment.controller.request.PaymentCreateRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentConfirmRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentFailCallbackRequest;
import com.samyookgoo.palgoosam.payment.controller.response.PaymentCreateResponse;
import com.samyookgoo.palgoosam.payment.controller.response.TossPaymentConfirmResponse;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.exception.PaymentExternalException;
import com.samyookgoo.palgoosam.payment.exception.PaymentInvalidStateException;
import com.samyookgoo.palgoosam.payment.exception.PaymentNotFoundException;
import com.samyookgoo.palgoosam.payment.policy.DeliveryPolicy;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

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
                .orElseThrow(AuctionNotFoundException::new);

        Bid winningBid = bidRepository.findByAuctionIdAndIsWinningTrue(auctionId)
                .orElseThrow(() -> new BidNotFoundException(ErrorCode.WINNING_BID_NOT_FOUND));

        winningBid.validatePaymentConditions(buyer.getId(), request.getItemPrice());

        if (paymentRepository.existsByAuctionIdAndStatusIn(auctionId,
                List.of(PaymentStatus.PAID, PaymentStatus.PENDING))) {
            throw new PaymentInvalidStateException(ErrorCode.PAYMENT_IN_PROGRESS_OR_DONE);
        }

        deliveryPolicy.validateDeliveryFee(request.getItemPrice(), request.getDeliveryFee());

        Payment payment = paymentRepository.findByAuction_Id(auctionId)
                .orElseGet(() -> {
                    Integer deliveryFee = request.getDeliveryFee();
                    String orderNumber = generateOrderNumber(auctionId);
                    Payment newPayment = Payment.of(auction, buyer, request, deliveryFee, orderNumber);
                    return paymentRepository.save(newPayment);
                });

        return PaymentCreateResponse.from(payment, auction.getTitle(), request.getSuccessUrl(), request.getFailUrl());
    }

    @Transactional
    public void failPayment(TossPaymentFailCallbackRequest request) {
        Payment payment = paymentRepository.findByOrderNumber(request.getOrderNumber())
                .orElseThrow(PaymentNotFoundException::new);

        payment.markAsFailed();
    }

    @Transactional
    public TossPaymentConfirmResponse confirmPayment(TossPaymentConfirmRequest request) {
        Payment payment = paymentRepository.findByOrderNumber(request.getOrderId())
                .orElseThrow(PaymentNotFoundException::new);

        payment.validatePaymentConditions(request.getAmount());

        String url = config.getBaseUrl() + "/v1/payments/confirm";

        payment.setType(request.getPaymentType());
        payment.setPaymentKey(request.getPaymentKey());

        try {
            TossPaymentConfirmResponse tossResponse = tossRestTemplate.postForObject(url, request,
                    TossPaymentConfirmResponse.class);

            if (tossResponse == null) {
                throw new PaymentExternalException(ErrorCode.TOSS_PAYMENT_EMPTY_RESPONSE);
            }

            payment.markAsPaid(OffsetDateTime.parse(tossResponse.getApprovedAt()));

            return TossPaymentConfirmResponse.from(
                    tossResponse,
                    payment.getRecipientEmail(),
                    payment.getRecipientName(),
                    payment.getPhoneNumber()
            );

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            payment.markAsFailed();

            String responseBody = ex.getResponseBodyAsString();
            String code = "UNKNOWN";
            String message = ErrorCode.TOSS_PAYMENT_FAILED.getMessage();

            try {
                Map<String, Object> errorMap = objectMapper.readValue(responseBody, new TypeReference<>() {
                });
                code = errorMap.getOrDefault("code", code).toString();
                message = errorMap.getOrDefault("message", message).toString();
            } catch (Exception parseEx) {
                log.warn("Toss 결제 오류 응답 파싱 실패: {}", parseEx.getMessage());
            }

            log.error("Toss 결제 승인 실패 - code: {}, message: {}", code, message);
            throw new PaymentExternalException(ErrorCode.TOSS_PAYMENT_FAILED);

        } catch (Exception ex) {
            payment.markAsFailed();
            if (ex instanceof PaymentExternalException) {
                throw ex;
            }
            log.error("Toss 결제 처리 중 예외 발생", ex);
            throw new PaymentExternalException(ErrorCode.TOSS_PAYMENT_FAILED);
        }
    }

    public String generateOrderNumber(Long auctionId) {
        return String.format("ORD-%d-%s", auctionId, UUID.randomUUID().toString().substring(0, 8));
    }
}
