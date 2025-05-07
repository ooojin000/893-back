package com.samyookgoo.palgoosam.auction.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionCreateRequest {
    private String title;
    private String description;
    private int basePrice;
    private String itemCondition;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long categoryId;
    private Integer mainImageIndex; // 대표 이미지 인덱스 (프론트에서 클릭한 이미지가 대표 이미지로 설정됨 / 인덱스 0 ~ 9)
}