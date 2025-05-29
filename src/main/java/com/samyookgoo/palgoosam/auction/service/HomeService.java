package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.dto.home.DashboardResponse;
import com.samyookgoo.palgoosam.auction.dto.home.UpcomingAuctionResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import com.samyookgoo.palgoosam.auction.dto.home.RecentAuctionResponse;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
}
