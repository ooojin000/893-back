package com.samyookgoo.palgoosam.user.dto;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
            Auction auction,
            String mainImageUrl,
            Integer highestBid
    ) {
        return new UserAuctionsResponseDto(
                auction.getId(),
                highestBid,
                auction.getTitle(),
                auction.getEndTime(),
                auction.getStartTime(),
                auction.getStatus(),
                mainImageUrl
        );
    }
}