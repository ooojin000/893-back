package com.samyookgoo.palgoosam.auction.dto.home;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponse {
    private long totalUserCount;     // 현재 이용자 수
    private long totalAuctionCount;  // 현재 등록된 경매 수
    private long activeAuctionCount; // 현재 진행중인 경매 수
}
