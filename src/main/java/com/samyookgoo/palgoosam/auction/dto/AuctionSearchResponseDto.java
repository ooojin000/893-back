package com.samyookgoo.palgoosam.auction.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuctionSearchResponseDto {
    private Long id;
    private String title;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status;

    private Integer basePrice;
    private Integer currentPrice;

    private Integer bidderCount;

    private Integer scrapCount;

    private String thumbnailUrl;

    // 로그인 구현에 따라 수정 필요
    private Boolean isScrapped = false;
}
