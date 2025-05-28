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
import com.samyookgoo.palgoosam.payment.constant.PaymentStatus;
import com.samyookgoo.palgoosam.payment.controller.response.OrderResponse;
import com.samyookgoo.palgoosam.payment.policy.DeliveryPolicy;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final DeliveryAddressRepository deliveryAddressRepository;
    private final AuctionImageRepository auctionImageRepository;
    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final DeliveryPolicy deliveryPolicy;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long auctionId, Long userId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 경매가 존재하지 않습니다."));

        Bid winningBid = bidRepository.findByAuctionIdAndIsWinningTrue(auctionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 경매의 낙찰 정보가 존재하지 않습니다."));

        boolean isCurrentUserWinner = winningBid.getBidder().getId().equals(userId);

        if (!isCurrentUserWinner) {
            log.warn("비정상 접근: 유저({})가 낙찰자가 아님. auctionId={}", userId, auctionId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "낙찰자만 결제할 수 있습니다.");
        }

        DeliveryAddress deliveryAddress = deliveryAddressRepository.findDefaultByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "기본 배송지가 존재하지 않습니다. 배송지를 먼저 등록해주세요."));

        AuctionImage image = auctionImageRepository.findMainImageByAuctionId(auctionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 경매의 대표 이미지가 존재하지 않습니다."));

        final int itemPrice = winningBid.getPrice();
        final int deliveryFee = deliveryPolicy.calculate(itemPrice);
        final int finalPrice = itemPrice + deliveryFee;

        boolean hasBeenPaid = paymentRepository.existsByAuction_IdAndStatus(auctionId, PaymentStatus.PAID);

        if (hasBeenPaid) {
            log.warn("중복 결제 시도: 유저({})가 이미 결제 완료된 경매에 접근. auctionId={}", userId, auctionId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 결제가 완료된 경매입니다.");
        }

        return OrderResponse.builder()
                .auctionId(auction.getId())
                .customerName(winningBid.getBidder().getName())
                .auctionTitle(auction.getTitle())
                .auctionThumbnail(image.getUrl())
                .itemPrice(itemPrice)
                .deliveryFee(deliveryFee)
                .finalPrice(finalPrice)
                .deliveryAddress(DeliveryAddressResponseDto.of(deliveryAddress))
                .build();
    }
}
