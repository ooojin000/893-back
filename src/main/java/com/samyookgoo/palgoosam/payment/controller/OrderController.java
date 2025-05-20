package com.samyookgoo.palgoosam.payment.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.payment.controller.response.OrderResponse;
import com.samyookgoo.palgoosam.payment.service.PaymentService;
import com.samyookgoo.palgoosam.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
@Tag(name = "주문", description = "경매 상품 주문 정보 API")
public class OrderController {
    private final PaymentService paymentService;
    private final AuthService authService;

    @Operation(
            summary = "주문 정보 조회",
            description = "경매 ID에 해당하는 주문 정보를 조회합니다. 로그인한 사용자 기준으로 낙찰 정보를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "주문 정보 또는 경매 정보를 찾을 수 없음")
    })
    @GetMapping("/{auctionId}/orders")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderPage(
            @Parameter(name = "auctionId", description = "주문 정보를 조회할 경매 ID", required = true)
            @PathVariable Long auctionId
    ) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        OrderResponse response = paymentService.getOrder(auctionId, user.getId());
        return ResponseEntity.ok(BaseResponse.success("주문 정보 조회 성공", response)
        );
    }
}

