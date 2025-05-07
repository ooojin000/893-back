package com.samyookgoo.palgoosam.bid.service;

import com.samyookgoo.palgoosam.bid.controller.response.BidListResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResponse;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BidService {
    private final BidRepository bidRepository;
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
