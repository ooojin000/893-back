package com.samyookgoo.palgoosam.bid.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.exception.AuctionNotFoundException;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.controller.response.BidEventResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidListResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResultResponse;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.exception.BidBadRequestException;
import com.samyookgoo.palgoosam.bid.exception.BidForbiddenException;
import com.samyookgoo.palgoosam.bid.exception.BidInvalidStateException;
import com.samyookgoo.palgoosam.bid.exception.BidNotFoundException;
import com.samyookgoo.palgoosam.bid.projection.AuctionMaxBid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.exception.UserNotFoundException;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final SseService sseService;

    public Map<Long, Integer> getAuctionMaxPrices(List<Long> auctionIds) {
        return bidRepository
                .findMaxBidPricesByAuctionIds(auctionIds)
                .stream()
                .collect(Collectors.toMap(
                        AuctionMaxBid::getAuctionId,
                        AuctionMaxBid::getMaxPrice
                ));
    }

    public BidListResponse getBidsByAuctionId(Long auctionId, User user) {
        if (!auctionRepository.existsById(auctionId)) {
            throw new AuctionNotFoundException();
        }

        List<Bid> allBids = bidRepository.findByAuctionIdOrderByCreatedAtDesc(auctionId);

        List<BidResponse> activeBids = new ArrayList<>();
        List<BidResponse> cancelledBids = new ArrayList<>();
        BidResponse recentUserBid = null; // 유저의 최근 1분 내 입찰 정보. 비회원이거나 1분 내 입찰 없으면 null
        boolean canCancelBid = false;
        boolean isExistCancelled = false;

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        // 이전 입찰 취소 내역 존재 시, 취소 불가. recentUserBid = null
        if (user != null) {
            isExistCancelled = isExistCancelledBidBefore(auctionId, user.getId());
        }

        for (Bid bid : allBids) {
            BidResponse response = BidResponse.from(bid);

            if (Boolean.TRUE.equals(bid.getIsDeleted())) {
                cancelledBids.add(response);
                continue;
            }

            activeBids.add(response);

            if (user != null && !isExistCancelled) {
                if (recentUserBid == null && isRecentBidByUser(bid, user, oneMinuteAgo)) {
                    recentUserBid = response;
                    canCancelBid = true;
                }
            }
        }

        int totalBid = bidRepository.countByAuctionIdAndIsDeletedFalse(auctionId);
        int totalBidder = bidRepository.countDistinctBidderByAuctionId(auctionId);

        return BidListResponse.builder()
                .auctionId(auctionId)
                .totalBid(totalBid)
                .totalBidder(totalBidder)
                .canCancelBid(canCancelBid)
                .recentUserBid(recentUserBid)
                .bids(activeBids)
                .cancelledBids(cancelledBids)
                .build();
    }

    @Transactional
    public BidResultResponse placeBid(Long auctionId, Long userId, int price) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(AuctionNotFoundException::new);

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        validatePlaceBid(auction, user, price);

        clearPreviousWinningBid(auctionId);

        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(user)
                .price(price)
                .isWinning(Boolean.TRUE)
                .isDeleted(Boolean.FALSE)
                .build();

        Bid savedBid = bidRepository.save(bid);
        BidResponse bidResponse = BidResponse.from(savedBid);
        boolean canCancelBid = !isExistCancelledBidBefore(auctionId, userId);

        BidEventResponse event = createBidEventResponse(auctionId, bidResponse, false);
        sseService.broadcastBidUpdate(auctionId, event);

        return BidResultResponse.from(bidResponse, canCancelBid);
    }

    @Transactional
    public BidEventResponse cancelBid(Long auctionId, Long bidId, User currentUser) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(AuctionNotFoundException::new);

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(BidNotFoundException::new);

        validateCancelBid(auction, bid, currentUser);

        bid.setIsWinning(Boolean.FALSE);
        bid.setIsDeleted(Boolean.TRUE);

        updateWinningBid(auctionId);

        BidResponse bidResponse = BidResponse.from(bid);

        return createBidEventResponse(auctionId, bidResponse, true);
    }

    private void clearPreviousWinningBid(Long auctionId) {
        bidRepository.findByAuctionIdAndIsWinningTrue(auctionId)
                .ifPresent(prev -> prev.setIsWinning(false));
    }

    private void updateWinningBid(Long auctionId) {
        bidRepository.findTopValidBidByAuctionId(auctionId)
                .ifPresent(newWinner -> {
                    newWinner.setIsWinning(true);
                    bidRepository.save(newWinner);
                });
    }

    private BidEventResponse createBidEventResponse(Long auctionId, BidResponse bidResponse, boolean isCancelled) {
        Integer currentPrice = bidRepository.findMaxBidPriceByAuctionId(auctionId);
        int totalBid = bidRepository.countByAuctionIdAndIsDeletedFalse(auctionId);
        int totalBidder = bidRepository.countDistinctBidderByAuctionId(auctionId);

        return BidEventResponse.builder()
                .currentPrice(currentPrice)
                .totalBid(totalBid)
                .totalBidder(totalBidder)
                .isCancelled(isCancelled)
                .bid(bidResponse)
                .build();
    }

    private boolean isRecentBidByUser(Bid bid, User user, LocalDateTime oneMinuteAgo) {
        return bid.getBidder().getId().equals(user.getId()) && bid.getCreatedAt().isAfter(oneMinuteAgo);
    }

    private boolean isExistCancelledBidBefore(Long auctionId, Long userId) {
        return bidRepository.existsByAuctionIdAndBidderIdAndIsDeletedTrue(auctionId, userId);
    }

    private void validatePlaceBid(Auction auction, User user, int price) {
        if (auction.getSeller().getId().equals(user.getId())) {
            throw new BidForbiddenException(ErrorCode.SELLER_CANNOT_BID);
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new BidInvalidStateException(ErrorCode.BID_TIME_INVALID);
        }

        if (price < auction.getBasePrice()) {
            throw new BidBadRequestException(ErrorCode.BID_LESS_THAN_BASE);
        }

        Integer highestPrice = bidRepository.findMaxBidPriceByAuctionId(auction.getId());
        if (highestPrice != null && price <= highestPrice) {
            throw new BidBadRequestException(ErrorCode.BID_NOT_HIGHEST);
        }
    }

    private void validateCancelBid(Auction auction, Bid bid, User currentUser) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new BidInvalidStateException(ErrorCode.BID_TIME_INVALID);
        }

        if (!bid.getBidder().getId().equals(currentUser.getId())) {
            throw new BidForbiddenException(ErrorCode.BID_CANCEL_FORBIDDEN);
        }

        if (Boolean.TRUE.equals(bid.getIsDeleted())) {
            throw new BidInvalidStateException(ErrorCode.BID_ALREADY_CANCELED);
        }

        if (isExistCancelledBidBefore(auction.getId(), bid.getBidder().getId())) {
            throw new BidInvalidStateException(ErrorCode.BID_CANCEL_LIMIT_EXCEEDED);
        }

        if (now.isAfter(bid.getCreatedAt().plusMinutes(1))) {
            throw new BidInvalidStateException(ErrorCode.BID_CANCEL_EXPIRED);
        }
    }
}
