package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.ItemCondition;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuctionUpdatePageResponse {
    private Long auctionId;
    private Long categoryId;
    private String title;
    private String description;
    private ItemCondition itemCondition;
    private AuctionStatus status;

    private CategoryResponse category;

    private AuctionImageResponse mainImage;
    private List<AuctionImageResponse> images;
}
