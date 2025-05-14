package com.samyookgoo.palgoosam.user.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.dto.UserAuctionsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserBidsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserPaymentsResponseDto;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final ScrapRepository scrapRepository;
    private final PaymentRepository paymentRepository;

    public List<UserBidsResponseDto> getUserBidsResponse (
            List<Bid> bids,
            Map<Long, String> imageMap,
            Map<Long, Integer> maxBidMap
    ) {
        return bids.stream()
                .map(bid1 -> UserBidsResponseDto.of(
                        bid1,
                        imageMap.getOrDefault(bid1.getAuction().getId(), ""),
                        maxBidMap.getOrDefault(bid1.getAuction().getId(), 0)
                ))
                .toList();
    }

    public List<UserAuctionsResponseDto> getUserAuctionsResponse (
            List<Auction> auctions,
            Map<Long, String> imageMap,
            Map<Long, Integer> maxBidMap
    ) {
        return auctions.stream()
                .map(a -> UserAuctionsResponseDto.of(
                        a,
                        imageMap.getOrDefault(a.getId(), ""),
                        maxBidMap.getOrDefault(a.getId(), 0)
                ))
                .toList();
    }

    public List<UserPaymentsResponseDto> getUserPaymentsResponse (
            List<Payment> payments,
            Map<Long, String> imageMap
    ) {
        return payments.stream()
                .map(p -> UserPaymentsResponseDto.of(
                        p,
                        p.getAuction().getId(),
                        imageMap.getOrDefault(p.getAuction().getId(), ""),
                        p.getAuction().getTitle()
                ))
                .toList();
    }

    public List<Bid> getUserBidsByUserId (Long userId) {
        return bidRepository.findAllByBidder_Id(userId);
    }

    public List<Auction> getUserAuctionsByUserId (Long userId) {
        return auctionRepository.findAllBySeller_Id(userId);
    }

    public List<Scrap> getUserScrapsByUserId (Long userId) {
        return scrapRepository.findAllByUser_Id(userId);
    }

    public List<Payment> getUserPaymentsByUserId (Long userId) {
        return paymentRepository.findAllByBuyer_Id(userId);
    }
}
