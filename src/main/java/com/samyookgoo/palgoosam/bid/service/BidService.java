package com.samyookgoo.palgoosam.bid.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.controller.response.BidListResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResponse;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
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

//    TODO: auctionRepository 사용 예정
//    private final AuctionRepository auctionRepository;

    public BidListResponse getBidsByAuctionId(Long auctionId) {
//        TODO: auctionRepository 사용 예정 - 경매 존재 여부 확인
//        if (!auctionRepository.existsById(auctionId)) {
//            throw new IllegalArgumentException("해당 경매를 찾을 수 없습니다.");
//        }

        List<Bid> allBids = bidRepository.findByAuctionIdOrderByCreatedAtDesc(auctionId);

        List<BidResponse> bids = allBids.stream()
                .filter(b -> b.getCancelledAt() == null)
                .map(this::mapToResponse)
                .toList();

        List<BidResponse> cancelledBids = allBids.stream()
                .filter(b -> b.getCancelledAt() != null)
                .map(this::mapToResponse)
                .toList();

        int totalBid = allBids.size();
        int totalBidder = (int) allBids.stream()
                .map(b -> b.getBidder().getId())
                .distinct()
                .count();

        return BidListResponse.builder()
                .auctionId(auctionId)
                .totalBid(totalBid)
                .totalBidder(totalBidder)
                .bids(bids)
                .cancelledBids(cancelledBids)
                .build();
    }

    @Transactional
    public BidResponse placeBid(Long auctionId, Long userId, int price) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("해당 경매를 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(auction.getStartTime()) || now.isAfter(auction.getEndTime())) {
            throw new IllegalStateException("현재는 입찰 가능한 시간이 아닙니다.");
        }

        if (auction.getBasePrice() > price) {
            throw new IllegalArgumentException("시작가보다 높은 금액을 입력해야 합니다.");
        }

        Integer highestPrice = bidRepository.findMaxBidPriceByAuctionId(auctionId);
        if (highestPrice != null && price <= highestPrice) {
            throw new IllegalArgumentException("현재 최고가보다 높은 금액을 입력해야 합니다.");
        }

        Optional<Bid> prevWinningBidOpt = bidRepository.findByAuctionIdAndIsWinningTrue(auctionId);
        prevWinningBidOpt.ifPresent(prev -> {
            prev.setIsWinning(false);
            bidRepository.save(prev);
        });

        Bid bid = Bid.builder()
                .auction(auction)
                .bidder(user)
                .price(price)
                .isWinning(Boolean.TRUE)
                .isDeleted(Boolean.FALSE)
                .build();

        Bid savedBid = bidRepository.save(bid);

        return mapToResponse(savedBid);
    }

    public void cancelBid(Long auctionId, Long bidId, User currentUser) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new NoSuchElementException("해당 입찰 내역이 존재하지 않습니다."));

        if (!bid.getBidder().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 입찰만 취소할 수 있습니다.");
        }

        if (bid.getCancelledAt() != null || bid.getIsDeleted() == Boolean.TRUE) {
            throw new IllegalStateException("이미 취소된 입찰입니다.");
        }

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        if (bid.getCreatedAt().isBefore(oneMinuteAgo)) {
            throw new IllegalStateException("입찰 후 1분 이내에만 취소할 수 있습니다.");
        }

        Integer highestPrice = bidRepository.findMaxBidPriceByAuctionId(auctionId);
        if (!Objects.equals(bid.getPrice(), highestPrice)) {
            throw new IllegalStateException("최고 입찰가가 아니면 취소할 수 없습니다.");
        }

        bid.setCancelledAt(LocalDateTime.now());
        bid.setIsWinning(Boolean.FALSE);
        bid.setIsDeleted(Boolean.TRUE);
        bidRepository.save(bid);

        Optional<Bid> newWinningBidOpt = bidRepository.findTopValidBidByAuctionId(auctionId);

        newWinningBidOpt.ifPresent(newWinningBid -> {
            newWinningBid.setIsWinning(true);
            bidRepository.save(newWinningBid);
        });
    }

    private BidResponse mapToResponse(Bid bid) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return BidResponse.builder()
                .bidId(bid.getId())
                .bidderEmail(bid.getBidder().getEmail())
                .bidPrice(bid.getPrice())
                .createdAt(bid.getCreatedAt().format(formatter))
                .cancelledAt(bid.getCancelledAt() != null ? bid.getCancelledAt().format(formatter) : null)
                .build();
    }

}
