package com.samyookgoo.palgoosam.auction.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuctionSearchResponseDto {
    private Long totalAuctionsCount;

    private List<AuctionListItemDto> auctionList;
}
