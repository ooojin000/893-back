package com.samyookgoo.palgoosam.auction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ImageDto {
    private String originalName;
    private String storeName;
    private Integer imageSeq;
}
