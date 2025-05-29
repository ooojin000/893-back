package com.samyookgoo.palgoosam.auction.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionSearchProjection {
    private Long id;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer basePrice;
    private String thumbnailUrl;
    private Integer currentPrice;
    private Long bidderCount;
    private Long scrapCount;
}