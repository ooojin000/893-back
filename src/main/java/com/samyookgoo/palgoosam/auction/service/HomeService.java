package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.dto.home.ActiveRankingResponse;
import com.samyookgoo.palgoosam.auction.dto.home.BestItemResponse;
import com.samyookgoo.palgoosam.auction.dto.home.DashboardResponse;
import com.samyookgoo.palgoosam.auction.dto.home.PendingRankingResponse;
import com.samyookgoo.palgoosam.auction.dto.home.RecentAuctionResponse;
import com.samyookgoo.palgoosam.auction.dto.home.SubCategoryBestItemResponse;
import com.samyookgoo.palgoosam.auction.dto.home.TopBidResponse;
import com.samyookgoo.palgoosam.auction.dto.home.UpcomingAuctionResponse;
import com.samyookgoo.palgoosam.auction.projection.AuctionBidCount;
import com.samyookgoo.palgoosam.auction.projection.AuctionScrapCount;
import com.samyookgoo.palgoosam.auction.projection.DashboardProjection;
import com.samyookgoo.palgoosam.auction.projection.RankingAuction;
import com.samyookgoo.palgoosam.auction.projection.RecentAuction;
import com.samyookgoo.palgoosam.auction.projection.SubCategoryBestItem;
import com.samyookgoo.palgoosam.auction.projection.TopWinningBid;
import com.samyookgoo.palgoosam.auction.projection.UpcomingAuction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    private final AuctionRepository auctionRepository;
    private final ScrapRepository scrapRepository;
    private final BidRepository bidRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        DashboardProjection counts = auctionRepository.getDashboardCounts();

        return DashboardResponse.builder()
                .totalUserCount(counts.getTotalUserCount())
                .totalAuctionCount(counts.getTotalAuctionCount())
                .activeAuctionCount(counts.getActiveAuctionCount())
                .build();
    }

    @Transactional(readOnly = true)
    public List<RecentAuctionResponse> getRecentAuctions() {
        List<AuctionStatus> statuses = List.of(AuctionStatus.active, AuctionStatus.pending);

        List<RecentAuction> recentAuctions = auctionRepository.findTop6RecentAuctions(statuses, PageRequest.of(0, 6));

        return recentAuctions.stream()
                .map(RecentAuctionResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UpcomingAuctionResponse> getUpcomingAuctions() {
        List<UpcomingAuction> upcomingAuctions = auctionRepository.findTop3UpcomingAuctions(
                AuctionStatus.pending, PageRequest.of(0, 3));

        Map<Long, Integer> scrapCountMap = getScrapCounts(upcomingAuctions);

        return upcomingAuctions.stream()
                .map(u -> UpcomingAuctionResponse.from(
                        u,
                        scrapCountMap.getOrDefault(u.getAuctionId(), 0),
                        formatLeftTime(u.getStartTime())
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TopBidResponse> getTopBid() {
        List<TopWinningBid> topWinningBidList = bidRepository.findTop5WinningBids();

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
                .map(t -> TopBidResponse.from(
                        t,
                        maskName(t.getBuyer()),
                        bidCountMap.getOrDefault(t.getAuctionId(), 0),
                        rank.getAndIncrement()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ActiveRankingResponse> getActiveRanking() {
        Pageable topEight = PageRequest.of(0, 8);
        List<AuctionBidCount> bidCounts = auctionRepository.findTop8AuctionBidCounts(topEight);

        List<Long> auctionIds = bidCounts.stream()
                .map(AuctionBidCount::getAuctionId)
                .toList();

        List<RankingAuction> rankingList = auctionRepository.findRankingByIds(auctionIds);

        Map<Long, RankingAuction> rankingMap = rankingList.stream()
                .collect(Collectors.toMap(RankingAuction::getAuctionId, r -> r));

        AtomicInteger rank = new AtomicInteger(1);

        return bidCounts.stream()
                .map(b -> {
                    RankingAuction r = rankingMap.get(b.getAuctionId());
                    return ActiveRankingResponse.from(
                            r,
                            b.getBidCount(),
                            rank.getAndIncrement()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PendingRankingResponse> getPendingRanking() {
        Pageable topEight = PageRequest.of(0, 8);
        List<AuctionScrapCount> scrapCounts = auctionRepository.findTop8AuctionScrapCounts(topEight);

        List<Long> auctionIds = scrapCounts.stream()
                .map(AuctionScrapCount::getAuctionId)
                .toList();

        List<RankingAuction> rankingList = auctionRepository.findRankingByIds(auctionIds);

        Map<Long, RankingAuction> rankingMap = rankingList.stream()
                .collect(Collectors.toMap(RankingAuction::getAuctionId, r -> r));

        AtomicInteger rank = new AtomicInteger(1);

        return scrapCounts.stream()
                .map(b -> {
                    RankingAuction r = rankingMap.get(b.getAuctionId());
                    return PendingRankingResponse.from(
                            r,
                            b.getScrapCount(),
                            rank.getAndIncrement()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SubCategoryBestItemResponse> getSubCategoryBestItem() {
        List<Category> subCategoryList = categoryRepository.findByParentIsNotNullAndChildrenIsNotEmpty();

        return subCategoryList.stream()
                .map(sub -> {
                    List<SubCategoryBestItem> itemProjections = auctionRepository.findTop50BySubCategoryId(sub.getId(),
                            PageRequest.of(0, 50));
                    List<AuctionScrapCount> scrapCounts = scrapRepository.countGroupedByAuctionIds(
                            itemProjections.stream().map(SubCategoryBestItem::getAuctionId).toList()
                    );
                    Map<Long, Integer> scrapMap = scrapCounts.stream()
                            .collect(Collectors.toMap(AuctionScrapCount::getAuctionId,
                                    AuctionScrapCount::getScrapCount));

                    AtomicInteger rank = new AtomicInteger(1);
                    List<BestItemResponse> items = itemProjections.stream()
                            .map(p -> BestItemResponse.from(
                                    p,
                                    isAuctionImminent(p.getStartTime()),
                                    scrapMap.getOrDefault(p.getAuctionId(), 0),
                                    rank.getAndIncrement()

                            ))
                            .toList();

                    return SubCategoryBestItemResponse.builder()
                            .subCategoryId(sub.getId())
                            .subCategoryName(sub.getName())
                            .items(items)
                            .build();
                }).toList();
    }

    private boolean isAuctionImminent(LocalDateTime startTime) {
        LocalDateTime now = LocalDateTime.now();
        return startTime.isAfter(now) && Duration.between(now, startTime).toHours() <= 1;
    }

    private Map<Long, Integer> getScrapCounts(List<UpcomingAuction> auctionList) {
        List<Long> auctionIds = auctionList.stream()
                .map(UpcomingAuction::getAuctionId)
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
            masked.append("*".repeat(length - 2));
            masked.append(name.charAt(length - 1));
            return masked.toString();
        }

        return name;
    }
}
