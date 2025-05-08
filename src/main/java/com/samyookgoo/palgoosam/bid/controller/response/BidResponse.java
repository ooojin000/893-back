package com.samyookgoo.palgoosam.bid.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidResponse {
    private Long bidId;
    private String bidderEmail;
    private Integer bidPrice;
    private String createdAt;
    private String updatedAt;
}
