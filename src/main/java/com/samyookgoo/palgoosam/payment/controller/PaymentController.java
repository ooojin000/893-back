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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "결제", description = "경매 결제 처리 API")
public class PaymentController {
    private final PaymentService paymentService;
    private final AuthService authService;

    @Operation(
            summary = "결제 요청 생성",
            description = "낙찰된 경매에 대해 결제를 요청합니다. 사용자는 요청 정보를 바탕으로 결제 페이지로 이동하게 됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 요청 생성 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "400", description = "요청 파라미터 오류 또는 유효하지 않은 상태")
    })
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
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        PaymentCreateResponse response = paymentService.createPayment(auctionId, user, request);
        return BaseResponse.success(response);
    }

    @Operation(
            summary = "결제 실패 처리",
            description = "결제 도중 실패한 경우 해당 실패 정보를 백엔드에 전달합니다. 이는 로그 또는 상태 업데이트 용도로 사용됩니다."
    )
    @ApiResponse(responseCode = "200", description = "결제 실패 처리 완료")
    @PostMapping("/payments/fail")
    public BaseResponse<Void> failPayment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "결제 실패 요청 정보", required = true)
            @RequestBody TossPaymentFailCallbackRequest request
    ) {
        paymentService.handlePaymentFailure(request);

        return BaseResponse.success(null);
    }

    @Operation(
            summary = "결제 승인 처리",
            description = "PG사 결제가 완료된 후, 클라이언트에서 전달한 정보를 바탕으로 최종 승인 및 DB 기록을 처리합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 승인 성공"),
            @ApiResponse(responseCode = "400", description = "승인 정보가 유효하지 않거나 승인 실패")
    })
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
