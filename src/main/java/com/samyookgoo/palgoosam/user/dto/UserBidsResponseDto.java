package com.samyookgoo.palgoosam.user.dto;

import com.samyookgoo.palgoosam.bid.domain.Bid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UserBidsResponseDto {
    private Long bidId;
    private Boolean isWinning;
    private Integer bidHighestPrice;
    private Integer userPrice;
    private String title;
    private LocalDateTime endTime;
    private LocalDateTime startTime;
    private String status;
    private Long auctionId;
    private String mainImageUrl;

    public static UserBidsResponseDto of(
            Bid bid,
            String mainImageUrl,
            Integer highestBid
    ) {
        return new UserBidsResponseDto(
                bid.getId(),
                bid.getIsWinning(),
                highestBid,
                bid.getPrice(),
                bid.getAuction().getTitle(),
                bid.getAuction().getEndTime(),
                bid.getAuction().getStartTime(),
                bid.getAuction().getStatus(),
                bid.getAuction().getId(),
                mainImageUrl
        );
    }
}
