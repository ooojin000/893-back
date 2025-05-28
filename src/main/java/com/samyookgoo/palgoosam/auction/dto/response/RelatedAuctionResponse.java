package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "연관 경매 상품 응답 DTO")
public class RelatedAuctionResponse {

    @Schema(description = "경매 ID", example = "101")
    private Long id;

    @Schema(description = "경매 제목", example = "아이패드 미니 6세대")
    private String title;

    @Schema(description = "경매 상태", example = "ACTIVE",
            allowableValues = {"PENDING", "ACTIVE", "COMPLETED", "CANCELLED"})
    private String status;

    @Schema(description = "대표 썸네일 이미지 URL", example = "https://cdn.893auction.com/images/thumbnail_101.jpg")
    private String thumbnailUrl;

    @Schema(description = "경매 종료 시간 (ISO 8601 형식)", example = "2025-06-01 12:00:00")
    private String endTime;

    @Schema(description = "현재 경매가", example = "150000")
    private Integer currentPrice;

    @Schema(description = "입찰 수", example = "7")
    private Integer bidderCount;

    @Schema(description = "스크랩 수", example = "23")
    private Integer scrapCount;

    @Schema(description = "현재 사용자가 이 상품을 스크랩했는지 여부", example = "true")
    private Boolean isScraped;

    public static RelatedAuctionResponse of(Auction auction,
                                            String thumbnailUrl,
                                            Long loginUserId,
                                            ScrapRepository scrapRepository,
                                            BidRepository bidRepository) {

        boolean isScraped =
                loginUserId != null && scrapRepository.existsByUserIdAndAuctionId(loginUserId, auction.getId());
        int scrapCount = scrapRepository.countByAuctionId(auction.getId());
        int bidCount = bidRepository.countByAuctionIdAndIsDeletedFalse(auction.getId());
        Integer winningPrice = bidRepository.findMaxBidPriceByAuctionId(auction.getId());

        return RelatedAuctionResponse.builder()
                .id(auction.getId())
                .title(auction.getTitle())
                .status(auction.getStatus().name().toLowerCase()) // pending, active, etc.
                .thumbnailUrl(thumbnailUrl)
                .endTime(auction.getEndTime().toString())
                .currentPrice((winningPrice != null && bidCount > 0) ? winningPrice : auction.getBasePrice())
                .bidderCount(bidCount)
                .scrapCount(scrapCount)
                .isScraped(isScraped)
                .build();
    }

}
