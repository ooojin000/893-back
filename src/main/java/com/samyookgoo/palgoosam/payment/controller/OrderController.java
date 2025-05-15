package com.samyookgoo.palgoosam.payment.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.payment.controller.response.OrderResponse;
import com.samyookgoo.palgoosam.payment.service.PaymentService;
import com.samyookgoo.palgoosam.user.domain.User;
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
public class OrderController {
    private final PaymentService paymentService;
    private final AuthService authService;

    @GetMapping("/{auctionId}/orders")
    public ResponseEntity<BaseResponse<OrderResponse>> getOrderPage(@PathVariable Long auctionId) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        OrderResponse response = paymentService.getOrder(auctionId, user.getId());
        return ResponseEntity.ok(BaseResponse.success("주문 정보 조회 성공", response)
        );
    }

}

