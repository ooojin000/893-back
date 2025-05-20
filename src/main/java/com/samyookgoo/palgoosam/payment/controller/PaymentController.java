package com.samyookgoo.palgoosam.payment.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.controller.response.BaseResponse;
import com.samyookgoo.palgoosam.payment.controller.request.PaymentCreateRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentConfirmRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentFailCallbackRequest;
import com.samyookgoo.palgoosam.payment.controller.response.PaymentCreateResponse;
import com.samyookgoo.palgoosam.payment.controller.response.TossPaymentConfirmResponse;
import com.samyookgoo.palgoosam.payment.service.PaymentService;
import com.samyookgoo.palgoosam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final AuthService authService;

    @PostMapping("/auctions/{auctionId}/payments")
    public BaseResponse<PaymentCreateResponse> create(@PathVariable Long auctionId,
                                                      @RequestBody PaymentCreateRequest request) {

        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        PaymentCreateResponse response = paymentService.createPayment(auctionId, user, request);
        return BaseResponse.success(response);
    }

    @PostMapping("/payments/fail")
    public BaseResponse<Void> failPayment(@RequestBody TossPaymentFailCallbackRequest request) {
        paymentService.handlePaymentFailure(request);

        return BaseResponse.success(null);
    }

    @PostMapping("/payments/confirm")
    public BaseResponse<TossPaymentConfirmResponse> confirmPayment(
            @RequestBody TossPaymentConfirmRequest request) {

        TossPaymentConfirmResponse response = paymentService.confirmPayment(request);

        return BaseResponse.success(response);
    }

}
