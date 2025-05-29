package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.dto.home.ActiveRankingResponse;
import com.samyookgoo.palgoosam.auction.dto.home.DashboardResponse;
import com.samyookgoo.palgoosam.auction.dto.home.RecentAuctionResponse;
import com.samyookgoo.palgoosam.auction.dto.home.TopBidResponse;
import com.samyookgoo.palgoosam.auction.dto.home.UpcomingAuctionResponse;
import com.samyookgoo.palgoosam.auction.projection.ActiveRanking;
import com.samyookgoo.palgoosam.auction.projection.AuctionBidCount;
import com.samyookgoo.palgoosam.auction.projection.AuctionScrapCount;
import com.samyookgoo.palgoosam.auction.projection.TopWinningBid;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
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
    public List<UpcomingAuctionResponse> getUpcomingAuctions() {
        List<Auction> auctionList = auctionRepository.findTop3ByStatusAndStartTimeAfterOrderByStartTimeAsc(
                AuctionStatus.pending, LocalDateTime.now());

        Map<Long, String> thumbnailMap = getThumbnailMap(auctionList);

        Map<Long, Integer> scrapCountMap = getScrapCounts(auctionList);

        return auctionList.stream()
                .map(auction -> UpcomingAuctionResponse.builder()
                        .auctionId(auction.getId())
                        .title(auction.getTitle())
                        .description(auction.getDescription())
                        .itemCondition(auction.getItemCondition())
                        .basePrice(auction.getBasePrice())
                        .scrapCount(scrapCountMap.getOrDefault(auction.getId(), 0))
                        .thumbnailUrl(thumbnailMap.get(auction.getId()))
                        .leftTime(formatLeftTime(auction.getStartTime()))
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

    @Transactional(readOnly = true)
    public List<ActiveRankingResponse> getActiveRanking() {
        Pageable topEight = PageRequest.of(0, 8);
        List<AuctionBidCount> bidCounts = auctionRepository.findTop8AuctionBidCounts(topEight);

        List<Long> auctionIds = bidCounts.stream()
                .map(AuctionBidCount::getAuctionId)
                .toList();

        List<ActiveRanking> rankingList = auctionRepository.findActiveRankingByIds(auctionIds);

        Map<Long, ActiveRanking> rankingMap = rankingList.stream()
                .collect(Collectors.toMap(ActiveRanking::getAuctionId, r -> r));

        return bidCounts.stream()
                .map(b -> {
                    ActiveRanking r = rankingMap.get(b.getAuctionId());
                    return ActiveRankingResponse.builder()
                            .auctionId(r.getAuctionId())
                            .title(r.getTitle())
                            .description(r.getDescription())
                            .itemCondition(r.getItemCondition())
                            .thumbnailUrl(r.getThumbnailUrl())
                            .bidCount(b.getBidCount())
                            .build();
                })
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

    private Map<Long, Integer> getScrapCounts(List<Auction> auctionList) {
        List<Long> auctionIds = auctionList.stream()
                .map(Auction::getId)
                .toList();

        if (auctionIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<AuctionScrapCount> result = scrapRepository.countGroupedByAuctionIds(auctionIds);

        return result.stream().collect(Collectors.toMap(
                AuctionScrapCount::getAuctionId,
                dto -> dto.getScrapCount().intValue()
        ));
    }

    private String formatLeftTime(LocalDateTime startTime) {
        Duration duration = Duration.between(LocalDateTime.now(), startTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
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
