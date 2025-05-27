package com.samyookgoo.palgoosam.bid.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.controller.response.BidEventResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidListResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResultResponse;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.projection.AuctionMaxBid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
            throw new NoSuchElementException("해당 경매를 찾을 수 없습니다.");
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
                .orElseThrow(() -> new NoSuchElementException("해당 경매를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new NoSuchElementException("해당 경매를 찾을 수 없습니다."));

        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new NoSuchElementException("해당 입찰 내역이 존재하지 않습니다."));

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
            throw new IllegalStateException("판매자는 자신의 경매에 입찰할 수 없습니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new IllegalStateException("현재는 입찰 가능한 시간이 아닙니다.");
        }

        if (price < auction.getBasePrice()) {
            throw new IllegalArgumentException("입찰 금액은 시작가 이상이어야 합니다.");
        }

        Integer highestPrice = bidRepository.findMaxBidPriceByAuctionId(auction.getId());
        if (highestPrice != null && price <= highestPrice) {
            throw new IllegalArgumentException("입찰 금액은 현재 최고가보다 높아야 합니다.");
        }
    }

    private void validateCancelBid(Auction auction, Bid bid, User currentUser) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new IllegalStateException("현재는 경매 진행 상태가 아닙니다.");
        }

        if (!bid.getBidder().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 입찰만 취소할 수 있습니다.");
        }

        if (Boolean.TRUE.equals(bid.getIsDeleted())) {
            throw new IllegalStateException("이미 취소된 입찰입니다.");
        }

        if (isExistCancelledBidBefore(auction.getId(), bid.getBidder().getId())) {
            throw new IllegalStateException("입찰 취소는 1번만 가능합니다.");
        }

        if (now.isAfter(bid.getCreatedAt().plusMinutes(1))) {
            throw new IllegalStateException("입찰 후 1분 이내에만 취소할 수 있습니다.");
        }
    }
}
