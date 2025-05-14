package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RelatedAuctionResponse {
    private Long auctionId;
    private String title;
    private String status;
    private String thumbnailUrl;
    private String timeLeft;
    private Integer price;
    private Integer bidCount;
    private Integer scrapCount;
    private boolean isScrapped;

    public static RelatedAuctionResponse of(Auction auction,
                                            String thumbnailUrl,
                                            Long loginUserId,
                                            ScrapRepository scrapRepository,
                                            BidRepository bidRepository) {

        String timeLeft = calculateTimeLeft(auction.getEndTime());
        boolean scrapped = scrapRepository.existsByUserIdAndAuctionId(loginUserId, auction.getId());
        int scrapCount = scrapRepository.countByAuctionId(auction.getId());

        int bidCount = bidRepository.countByAuctionId(auction.getId());

        Integer winningPrice = bidRepository.findMaxBidPriceByAuctionId(auction.getId());

        int price;
        if (bidCount == 0 || winningPrice == null) {
            price = auction.getBasePrice();
        } else {
            price = winningPrice;
        }

        return RelatedAuctionResponse.builder()
                .auctionId(auction.getId())
                .title(auction.getTitle())
                .status(auction.getStatus().name())
                .thumbnailUrl(thumbnailUrl)
                .timeLeft(timeLeft)
                .price(price)
                .bidCount(bidCount)
                .scrapCount(scrapCount)
                .isScrapped(scrapped)
                .build();
    }

    private static String calculateTimeLeft(LocalDateTime endTime) {
        Duration duration = Duration.between(LocalDateTime.now(), endTime);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
