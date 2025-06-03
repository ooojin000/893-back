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
import com.samyookgoo.palgoosam.user.exception.UserUnauthorizedException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final PaymentRepository paymentRepository;
    private final AuthService authService;

    public UserInfoResponseDto getUserInfo() {
        User user = getAuthenticatedUser(authService.getCurrentUser());
        return UserInfoResponseDto.from(user);
    }

    public List<UserBidsResponseDto> getUserBids() {
        User user = getAuthenticatedUser(authService.getCurrentUser());

        List<BidForHighestPriceProjection> bids = bidRepository.findHighestBidProjectsByBidderId(user.getId());
        Map<Long, Integer> maxBidMap = getMaxBidMap(bids);

        List<BidForMyPageProjection> bidProjections = bidRepository.findAllBidsByUserId(user.getId());

        return bidProjections.stream()
                .map(bidProjection -> UserBidsResponseDto.of(bidProjection,
                        maxBidMap.getOrDefault(bidProjection.getAuctionId(), 0))).toList();
    }

    public List<UserAuctionsResponseDto> getUserAuctions() {
        User user = getAuthenticatedUser(authService.getCurrentUser());

        List<AuctionForMyPageProjection> auctions = auctionRepository.findAllAuctionProjectionBySellerId(user.getId());

        List<BidForHighestPriceProjection> bids = bidRepository.findHighestBidProjectsBySellerId(user.getId());
        Map<Long, Integer> maxBidMap = getMaxBidMap(bids);

        return createUserAuctionsResponseDtoList(auctions, maxBidMap);
    }
// 이까지 급한 불은 껐다.

    public List<UserAuctionsResponseDto> getUserScraps() {
        User user = getAuthenticatedUser(authService.getCurrentUser());

        List<AuctionForMyPageProjection> auctions = auctionRepository.findAllAuctionProjectionWithScrapByUserId(
                user.getId());

        List<BidForHighestPriceProjection> bids = bidRepository.findHighestBidProjectsBySellerId(user.getId());
        Map<Long, Integer> maxBidMap = getMaxBidMap(bids);

        return createUserAuctionsResponseDtoList(auctions, maxBidMap);
    }

    public List<UserPaymentsResponseDto> getUserPayments() {
        User user = getAuthenticatedUser(authService.getCurrentUser());

        List<PaymentForMyPageProjection> payments = paymentRepository.findAllPaymentForMyPageProjectionByBuyerId(
                user.getId());

        return payments.stream().map(UserPaymentsResponseDto::of).toList();
    }

    public User getAuthenticatedUser(User user) {
        if (user == null) {
            throw new UserUnauthorizedException();
        }
        return user;
    }

    private Map<Long, Integer> getMaxBidMap(List<BidForHighestPriceProjection> bids) {
        return bids.stream().collect(Collectors.toMap(
                BidForHighestPriceProjection::getAuctionId,
                BidForHighestPriceProjection::getBidHighestPrice,
                (existing, replacement) -> existing
        ));
    }

    private List<UserAuctionsResponseDto> createUserAuctionsResponseDtoList(List<AuctionForMyPageProjection> auctions,
                                                                            Map<Long, Integer> maxBidMap) {
        return auctions.stream()
                .map(auctionProjection -> UserAuctionsResponseDto.of(
                        auctionProjection,
                        maxBidMap.getOrDefault(auctionProjection.getAuctionId(), 0)
                ))
                .toList();
    }
}
