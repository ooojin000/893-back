package com.samyookgoo.palgoosam.user.dto;

import com.samyookgoo.palgoosam.bid.domain.BidForMyPageProjection;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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
            BidForMyPageProjection bidProjection,
            Integer highestBid
    ) {
        return new UserBidsResponseDto(
                bidProjection.getBidId(),
                bidProjection.getIsWinning(),
                highestBid,
                bidProjection.getUserPrice(),
                bidProjection.getTitle(),
                bidProjection.getEndTime(),
                bidProjection.getStartTime(),
                bidProjection.getStatus().toString(),
                bidProjection.getAuctionId(),
                bidProjection.getMainImageUrl()
        );
    }
}
