package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.dto.home.DashboardResponse;
import com.samyookgoo.palgoosam.auction.dto.home.RecentAuctionResponse;
import com.samyookgoo.palgoosam.auction.dto.home.TopBidResponse;
import com.samyookgoo.palgoosam.auction.projection.AuctionBidCount;
import com.samyookgoo.palgoosam.auction.projection.TopWinningBid;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class HomeService {
    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final AuctionImageRepository auctionImageRepository;
    private final ScrapRepository scrapRepository;
    private final AuthService authService;
    private final BidRepository bidRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        long totalUserCount = userRepository.count();
        long totalAuctionCount = auctionRepository.count();
        long activeAuctionCount = auctionRepository.countByStatus(AuctionStatus.active);

        return DashboardResponse.builder()
                .totalUserCount(totalUserCount)
                .totalAuctionCount(totalAuctionCount)
                .activeAuctionCount(activeAuctionCount)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RecentAuctionResponse> getRecentAuctions() {
        User user = authService.getCurrentUser();

        List<Auction> auctionList = getRecentAuctionStatus();

        Map<Long, String> thumbnailMap = getThumbnailMap(auctionList);

        Set<Long> isScraped = getScrapedAuctionIds(user);

        return auctionList.stream()
                .map(auction -> RecentAuctionResponse.builder()
                        .auctionId(auction.getId())
                        .title(auction.getTitle())
                        .description(auction.getDescription())
                        .status(auction.getStatus())
                        .basePrice(auction.getBasePrice())
                        .thumbnailUrl(thumbnailMap.get(auction.getId()))
                        .isScraped(isScraped.contains(auction.getId()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TopBidResponse> getTopBid() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        Pageable topFive = PageRequest.of(0, 5);

        List<TopWinningBid> topWinningBidList = bidRepository.findTop5WinningBids(sevenDaysAgo, topFive);

        List<Long> auctionIds = topWinningBidList.stream()
                .map(TopWinningBid::getAuctionId)
                .toList();

        Map<Long, Integer> bidCountMap = bidRepository.countBidsByAuctionIds(auctionIds).stream()
                .collect(Collectors.toMap(
                        AuctionBidCount::getAuctionId,
                        AuctionBidCount::getBidCount
                ));

        AtomicInteger rank = new AtomicInteger(1);

        return topWinningBidList.stream()
                .map(t -> TopBidResponse.builder()
                        .auctionId(t.getAuctionId())
                        .title(t.getTitle())
                        .basePrice(t.getBasePrice())
                        .itemPrice(t.getItemPrice())
                        .thumbnailUrl(t.getThumbnailUrl())
                        .buyer(maskName(t.getBuyer()))
                        .bidCount(bidCountMap.getOrDefault(t.getAuctionId(), 0))
                        .rankNum(rank.getAndIncrement())
                        .build())
                .toList();
    }

    private List<Auction> getRecentAuctionStatus() {
        List<AuctionStatus> statuses = List.of(AuctionStatus.active, AuctionStatus.pending);
        return auctionRepository.findTop6ByStatusInOrderByCreatedAtDesc(statuses);
    }

    private Map<Long, String> getThumbnailMap(List<Auction> auctionList) {
        List<Long> auctionIds = auctionList.stream().map(Auction::getId).toList();
        return auctionImageRepository.findMainImagesByAuctionIds(auctionIds).stream()
                .collect(Collectors.toMap(
                        img -> img.getAuction().getId(),
                        AuctionImage::getUrl
                ));
    }

    private Set<Long> getScrapedAuctionIds(User user) {
        return user != null
                ? new HashSet<>(scrapRepository.findAuctionIdsByUserId(user.getId()))
                : Set.of();
    }

    public String maskName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }

        int length = name.length();

        if (length == 2) {
            return name.charAt(0) + "*";
        } else if (length >= 3) {
            StringBuilder masked = new StringBuilder();
            masked.append(name.charAt(0));
            for (int i = 1; i < length - 1; i++) {
                masked.append("*");
            }
            masked.append(name.charAt(length - 1));
            return masked.toString();
        }

        return name;
    }

}
