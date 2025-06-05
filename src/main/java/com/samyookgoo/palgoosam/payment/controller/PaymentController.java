package com.samyookgoo.palgoosam.payment.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.controller.response.BaseResponse;
import com.samyookgoo.palgoosam.payment.api_docs.ConfirmPaymentApi;
import com.samyookgoo.palgoosam.payment.api_docs.CreatePaymentApi;
import com.samyookgoo.palgoosam.payment.api_docs.PostFailPaymentApi;
import com.samyookgoo.palgoosam.payment.controller.request.PaymentCreateRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentConfirmRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentFailCallbackRequest;
import com.samyookgoo.palgoosam.payment.controller.response.PaymentCreateResponse;
import com.samyookgoo.palgoosam.payment.controller.response.TossPaymentConfirmResponse;
import com.samyookgoo.palgoosam.payment.service.PaymentService;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "결제", description = "경매 결제 처리 API")
public class PaymentController {
    private final PaymentService paymentService;
    private final AuthService authService;

    @CreatePaymentApi
    @PostMapping("/auctions/{auctionId}/payments")
    public BaseResponse<PaymentCreateResponse> create(
            @Parameter(name = "auctionId", description = "결제를 요청할 경매 ID", required = true)
            @PathVariable Long auctionId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "결제 생성 요청 정보", required = true)
            @RequestBody PaymentCreateRequest request
    ) {

        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UserNotFoundException();
        }
        PaymentCreateResponse response = paymentService.createPayment(auctionId, user, request);
        return BaseResponse.success(response);
    }

    @PostFailPaymentApi
    @PostMapping("/payments/fail")
    public BaseResponse<Void> failPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "결제 실패 요청 정보", required = true)
            @RequestBody TossPaymentFailCallbackRequest request
    ) {
        paymentService.handlePaymentFailure(request);

        return BaseResponse.success(null);
    }

    @ConfirmPaymentApi
    @PostMapping("/payments/confirm")
    public BaseResponse<TossPaymentConfirmResponse> confirmPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "결제 승인 요청 정보", required = true)
            @RequestBody TossPaymentConfirmRequest request
    ) {

        TossPaymentConfirmResponse response = paymentService.confirmPayment(request);

        return BaseResponse.success(response);
    }
}
