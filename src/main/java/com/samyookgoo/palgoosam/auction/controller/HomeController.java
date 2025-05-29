package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.dto.home.DashboardResponse;
import com.samyookgoo.palgoosam.auction.dto.home.TopBidResponse;
import com.samyookgoo.palgoosam.auction.dto.home.RecentAuctionResponse;
import com.samyookgoo.palgoosam.auction.service.HomeService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/home")
public class HomeController {
    private final HomeService homeService;

    @GetMapping("/dashboard")
    public DashboardResponse getDashboard() {
        return homeService.getDashboard();
    }

    @GetMapping("/recentAuction")
    public ResponseEntity<BaseResponse<List<RecentAuctionResponse>>> getRecentAuction() {
        List<RecentAuctionResponse> responses = homeService.getRecentAuctions();
        return ResponseEntity.ok(BaseResponse.success("최근 등록한 상품 목록 조회 성공", responses));
    }

    @GetMapping("topBid")
    public ResponseEntity<BaseResponse<List<TopBidResponse>>> getTopBid() {
        List<TopBidResponse> responses = homeService.getTopBid();
        return ResponseEntity.ok(BaseResponse.success("이번주 최고 낙찰가 top 5 조회 성공", responses));
    }
}
