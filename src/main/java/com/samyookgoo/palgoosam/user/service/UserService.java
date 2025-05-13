package com.samyookgoo.palgoosam.user.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.user.dto.UserAuctionsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserBidsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserPaymentsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
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
}
