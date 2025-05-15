package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RelatedAuctionResponse {
    private Long auctionId;
    private String title;
    private String status;
    private String thumbnailUrl;
    private String endTime;
    private Integer price;
    private Integer bidCount;
    private Integer scrapCount;
    private boolean isScrapped;

    public static RelatedAuctionResponse of(Auction auction,
                                            String thumbnailUrl,
                                            Long loginUserId,
                                            ScrapRepository scrapRepository,
                                            BidRepository bidRepository) {
        boolean scrapped;
        if (loginUserId != null) {
            scrapped = scrapRepository.existsByUserIdAndAuctionId(loginUserId, auction.getId());
        } else {
            scrapped = false;
        }
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
                .endTime(auction.getEndTime().toString())
                .price(price)
                .bidCount(bidCount)
                .scrapCount(scrapCount)
                .isScrapped(scrapped)
                .build();
    }

}
