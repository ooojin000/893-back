package com.samyookgoo.palgoosam.auction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuctionCreateRequest {
    private String title;
    private String description;
    private int basePrice;
    private itemCondition itemCondition;
    private int startDelay;
    private int durationTime;
    private Long categoryId;
    private Integer mainImageIndex; // 대표 이미지 인덱스 (프론트에서 클릭한 이미지가 대표 이미지로 설정됨 / 인덱스 0 ~ 9)
}