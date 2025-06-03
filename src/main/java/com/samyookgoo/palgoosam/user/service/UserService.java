package com.samyookgoo.palgoosam.user.service;

import com.samyookgoo.palgoosam.auction.domain.AuctionForMyPageProjection;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.domain.BidForHighestPriceProjection;
import com.samyookgoo.palgoosam.bid.domain.BidForMyPageProjection;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.payment.domain.PaymentForMyPageProjection;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.dto.UserAuctionsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserBidsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserInfoResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserPaymentsResponseDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final PaymentRepository paymentRepository;
    private final AuthService authService;

    public UserInfoResponseDto getUserInfo() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        return UserInfoResponseDto.from(user);
    }

    public List<UserBidsResponseDto> getUserBids() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<BidForHighestPriceProjection> bids = bidRepository.findHighestBidProjectsByBidderId(user.getId());
        Map<Long, Integer> maxBidMap = bids.stream().collect(Collectors.toMap(
                BidForHighestPriceProjection::getAuctionId,
                BidForHighestPriceProjection::getBidHighestPrice,
                (existing, replacement) -> existing
        ));

        List<BidForMyPageProjection> bidProjections = bidRepository.findAllBidsByUserId(user.getId());

        return bidProjections.stream()
                .map(bidProjection -> UserBidsResponseDto.of(bidProjection,
                        maxBidMap.getOrDefault(bidProjection.getAuctionId(), 0))).toList();
    }

    public List<UserAuctionsResponseDto> getUserAuctions() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<AuctionForMyPageProjection> auctions = auctionRepository.findAllAuctionProjectionBySellerId(user.getId());
        // 경매별 최고 입찰가
        List<BidForHighestPriceProjection> bids = bidRepository.findHighestBidProjectsBySellerId(user.getId());
        Map<Long, Integer> maxBidMap = bids.stream().collect(Collectors.toMap(
                BidForHighestPriceProjection::getAuctionId,
                BidForHighestPriceProjection::getBidHighestPrice,
                (existing, replacement) -> existing
        ));

        return auctions.stream()
                .map(auctionProjection -> UserAuctionsResponseDto.of(
                        auctionProjection,
                        maxBidMap.getOrDefault(auctionProjection.getAuctionId(), 0)
                ))
                .toList();
    }
// 이까지 급한 불은 껐다.

    public List<UserAuctionsResponseDto> getUserScraps() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }

        List<AuctionForMyPageProjection> auctions = auctionRepository.findAllAuctionProjectionWithScrapByUserId(
                user.getId());

        // 경매별 최고 입찰가
        List<BidForHighestPriceProjection> bids = bidRepository.findHighestBidProjectsBySellerId(user.getId());
        Map<Long, Integer> maxBidMap = bids.stream().collect(Collectors.toMap(
                BidForHighestPriceProjection::getAuctionId,
                BidForHighestPriceProjection::getBidHighestPrice,
                (existing, replacement) -> existing
        ));

        return auctions.stream()
                .map(auctionProjection -> UserAuctionsResponseDto.of(
                        auctionProjection,
                        maxBidMap.getOrDefault(auctionProjection.getAuctionId(), 0)
                ))
                .toList();
    }

    public List<UserPaymentsResponseDto> getUserPayments() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<PaymentForMyPageProjection> payments = paymentRepository.findAllPaymentForMyPageProjectionByBuyerId(
                user.getId());

        return payments.stream().map(UserPaymentsResponseDto::of).toList();
    }
}
