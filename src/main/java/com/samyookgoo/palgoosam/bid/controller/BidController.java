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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@Tag(name = "입찰", description = "경매 입찰 관련 API")
public class BidController {

    private final BidService bidService;
    private final AuthService authService;
    private final SseService sseService;

    @Operation(
            summary = "경매 입찰 내역 조회",
            description = "경매 ID를 기준으로 해당 경매의 모든 입찰 내역을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입찰 내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 경매를 찾을 수 없음")
    })
    @GetMapping("/{auctionId}/bids")
    public BaseResponse<BidListResponse> auctionBids(
            @Parameter(name = "auctionId", description = "입찰 내역을 조회할 경매 ID", required = true)
            @PathVariable Long auctionId
    ) {
        BidListResponse response = bidService.getBidsByAuctionId(auctionId);
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "경매 입찰 요청",
            description = "사용자가 특정 경매에 대해 입찰합니다. 입찰가는 현재 입찰가보다 높아야 하며, SSE로 실시간 반영됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입찰 성공"),
            @ApiResponse(responseCode = "400", description = "입찰가가 기준보다 낮거나 잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "유저 또는 경매를 찾을 수 없음")
    })
    @PostMapping("/{auctionId}/bids")
    public BaseResponse<BidResponse> placeBid(
            @Parameter(name = "auctionId", description = "입찰할 경매 ID", required = true)
            @PathVariable Long auctionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "입찰 요청 정보 (입찰가 포함)",
                    required = true
            )
            @Valid @RequestBody BidRequest request
    ) {// TODO: 인증 사용자 연동 시 수정 필요
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        BidEventResponse response = bidService.placeBid(auctionId, user.getId(), request.getPrice());
        sseService.broadcastBidUpdate(auctionId, response);

        return BaseResponse.success(response.getBid());
    }

    @Operation(
            summary = "입찰 취소",
            description = "사용자가 본인의 입찰을 취소합니다. SSE를 통해 취소 결과가 실시간으로 반영됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "입찰 취소 성공"),
            @ApiResponse(responseCode = "404", description = "유저, 경매, 또는 입찰 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "403", description = "다른 사용자의 입찰은 취소할 수 없음")
    })
    @CrossOrigin(origins = "http://localhost:3000")
    @PatchMapping("/{auctionId}/bids/{bidId}")
    public BaseResponse<String> cancelBid(
            @Parameter(name = "auctionId", description = "경매 ID", required = true)
            @PathVariable Long auctionId,

            @Parameter(name = "bidId", description = "취소할 입찰 ID", required = true)
            @PathVariable Long bidId
    ) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        BidEventResponse response = bidService.cancelBid(auctionId, bidId, user);
        sseService.broadcastBidUpdate(auctionId, response);

        return BaseResponse.success("입찰 취소 완료");
    }
}
