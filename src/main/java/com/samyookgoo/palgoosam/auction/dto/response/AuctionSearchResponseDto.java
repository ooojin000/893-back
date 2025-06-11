package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.dto.AuctionSearchResultDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuctionSearchResponseDto {
    private Long totalAuctionsCount;

    private List<AuctionSearchResultDto> auctionList;
}
