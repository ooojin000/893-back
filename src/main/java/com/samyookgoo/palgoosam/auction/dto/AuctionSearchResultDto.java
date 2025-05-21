package com.samyookgoo.palgoosam.auction.dto;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuctionSearchResultDto {
    private Long id;
    private String title;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private AuctionStatus status;

    private Integer basePrice;
    private Integer currentPrice;

    private Long bidderCount;

    private Long scrapCount;

    private String thumbnailUrl;

    // 로그인 구현에 따라 수정 필요
    private Boolean isScrapped = false;
}
