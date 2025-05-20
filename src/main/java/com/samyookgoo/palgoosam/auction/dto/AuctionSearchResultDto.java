package com.samyookgoo.palgoosam.auction.dto;

import com.samyookgoo.palgoosam.auction.category.domain.Category;
import com.samyookgoo.palgoosam.user.domain.User;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionSearchResultDto {
    private Long id;

    private User seller;

    private Category category;

    private String title;

    private String description;

    private Integer basePrice;
    private Integer currentPrice;

    private String itemCondition;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status;

    private Integer bidderCount;

    private Integer scrapCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
