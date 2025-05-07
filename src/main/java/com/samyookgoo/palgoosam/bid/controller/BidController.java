package com.samyookgoo.palgoosam.bid.controller;

import com.samyookgoo.palgoosam.bid.controller.response.BaseResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidListResponse;
import com.samyookgoo.palgoosam.bid.service.BidService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;

    @GetMapping("/{auctionId}/bids")
    public BaseResponse<BidListResponse> auctionBids(@PathVariable Long auctionId) {
        BidListResponse response = bidService.getBidsByAuctionId(auctionId);
        return BaseResponse.success(response);
    }
}
