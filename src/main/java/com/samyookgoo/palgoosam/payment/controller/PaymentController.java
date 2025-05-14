package com.samyookgoo.palgoosam.payment.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.controller.response.BaseResponse;
import com.samyookgoo.palgoosam.payment.controller.request.CreatePaymentRequest;
import com.samyookgoo.palgoosam.payment.controller.response.PaymentResponse;
import com.samyookgoo.palgoosam.payment.service.PaymentService;
import com.samyookgoo.palgoosam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class PaymentController {
    private final PaymentService paymentService;
    private final AuthService authService;

    @PostMapping("/{auctionId}/payments")
    public BaseResponse<PaymentResponse> create(@PathVariable Long auctionId,
                                                @RequestBody CreatePaymentRequest request) {

        User currentUser = authService.getCurrentUser();

        PaymentResponse response = paymentService.createPayment(auctionId, currentUser, request);
        return BaseResponse.success(response);
    }

}
