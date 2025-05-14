package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.ItemCondition;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionDetailResponse {
    private Long auctionId;
    private Long categoryId;
    private String title;
    private String description;
    private String sellerEmailMasked;
    private AuctionStatus status;
    private ItemCondition itemCondition;
    private Integer basePrice;

    private Boolean isScrap;
    private Integer scrapCount;

    private Boolean isSeller;

    private CategoryResponse category;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private AuctionImageResponse mainImage;
    private List<AuctionImageResponse> images;
}

