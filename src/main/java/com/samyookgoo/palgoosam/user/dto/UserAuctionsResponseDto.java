package com.samyookgoo.palgoosam.user.dto;

import com.samyookgoo.palgoosam.auction.domain.AuctionForMyPageProjection;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAuctionsResponseDto {
    private Long auctionId;
    private Integer bidHighestPrice;
    private String title;
    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private String status;
    private String mainImageUrl;

    public static UserAuctionsResponseDto of(
            AuctionForMyPageProjection auctionProjection,
            Integer highestBid
    ) {
        return new UserAuctionsResponseDto(
                auctionProjection.getAuctionId(),
                highestBid,
                auctionProjection.getTitle(),
                auctionProjection.getEndTime(),
                auctionProjection.getStartTime(),
                auctionProjection.getStatus().toString(),
                auctionProjection.getMainImageUrl()
        );
    }
}