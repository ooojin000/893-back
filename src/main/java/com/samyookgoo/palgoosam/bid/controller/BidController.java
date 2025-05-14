package com.samyookgoo.palgoosam.bid.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.controller.request.BidRequest;
import com.samyookgoo.palgoosam.bid.controller.response.BaseResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidEventResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidListResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResponse;
import com.samyookgoo.palgoosam.bid.service.BidService;
import com.samyookgoo.palgoosam.bid.service.SseService;
import com.samyookgoo.palgoosam.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auctions")
@RequiredArgsConstructor
public class BidController {

    private final BidService bidService;
    private final AuthService authService;
    private final SseService sseService;

    @GetMapping("/{auctionId}/bids")
    public BaseResponse<BidListResponse> auctionBids(@PathVariable Long auctionId) {
        BidListResponse response = bidService.getBidsByAuctionId(auctionId);
        return BaseResponse.success(response);
    }

    @PostMapping("/{auctionId}/bids")
    public BaseResponse<BidResponse> placeBid(@PathVariable Long auctionId,
                                              @Valid @RequestBody BidRequest request) {// TODO: 인증 사용자 연동 시 수정 필요

        User currentUser = authService.getCurrentUser();

        BidEventResponse response = bidService.placeBid(auctionId, currentUser.getId(), request.getPrice());
        sseService.broadcastBidUpdate(auctionId, response);

        return BaseResponse.success(response.getBid());
    }


    @PatchMapping("/{auctionId}/bids/{bidId}")
    public BaseResponse<String> cancelBid(@PathVariable Long auctionId, @PathVariable Long bidId) {

        User currentUser = authService.getCurrentUser();

        BidEventResponse response = bidService.cancelBid(auctionId, bidId, currentUser);
        sseService.broadcastBidUpdate(auctionId, response);

        return BaseResponse.success("입찰 취소 완료");
    }
}
