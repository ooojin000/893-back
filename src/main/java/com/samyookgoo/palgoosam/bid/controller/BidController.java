package com.samyookgoo.palgoosam.bid.controller;

import com.samyookgoo.palgoosam.bid.controller.request.BidRequest;
import com.samyookgoo.palgoosam.bid.controller.response.BaseResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidListResponse;
import com.samyookgoo.palgoosam.bid.controller.response.BidResponse;
import com.samyookgoo.palgoosam.bid.service.BidService;
import com.samyookgoo.palgoosam.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/{auctionId}/bids")
    public BaseResponse<BidListResponse> auctionBids(@PathVariable Long auctionId) {
        BidListResponse response = bidService.getBidsByAuctionId(auctionId);
        return BaseResponse.success(response);
    }

    @PostMapping("/{auctionId}/bids")
    public BaseResponse<BidResponse> placeBid(@PathVariable Long auctionId, @RequestBody BidRequest request) {
//        TODO: UserService 구현 후 수정 예정
        User currentUser = User.builder()
                .name("홍길동")
                .email("hong@gmail.com")
                .build();

        BidResponse response = bidService.placeBid(auctionId, currentUser.getId(), request.getPrice());
        return BaseResponse.success(response);
    }


    @PatchMapping("/{auctionId}/bids/{bidId}")
    public BaseResponse<String> cancelBid(@PathVariable Long auctionId, @PathVariable Long bidId,
                                          HttpServletRequest request) {
//        TODO: UserService 구현 후 수정 예정
        User currentUser = User.builder()
                .name("홍길동")
                .email("hong@gmail.com")
                .build();

        bidService.cancelBid(auctionId, bidId, currentUser);
        return BaseResponse.success("입찰");
    }
}
