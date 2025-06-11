package com.samyookgoo.palgoosam.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.payment.constant.PaymentType;
import com.samyookgoo.palgoosam.payment.controller.request.PaymentCreateRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentConfirmRequest;
import com.samyookgoo.palgoosam.payment.controller.request.TossPaymentFailCallbackRequest;
import com.samyookgoo.palgoosam.payment.controller.response.PaymentCreateResponse;
import com.samyookgoo.palgoosam.payment.controller.response.TossPaymentConfirmResponse;
import com.samyookgoo.palgoosam.payment.exception.PaymentExternalException;
import com.samyookgoo.palgoosam.payment.service.PaymentService;
import com.samyookgoo.palgoosam.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private AuthService authService;

    @DisplayName("구매자가 결제를 요청하면 주문이 생성된다.")
    @Test
    void shouldCreateOrderWhenBuyerRequestsPayment() throws Exception {
        // given
        Long auctionId = 1L;
        User user = createMockUser();

        PaymentCreateRequest request = createPaymentCreateRequest();

        PaymentCreateResponse response = PaymentCreateResponse.builder()
                .orderId("ORD-123")
                .orderName("Test Auction")
                .successUrl(request.getSuccessUrl())
                .failUrl(request.getFailUrl())
                .customerEmail(user.getEmail())
                .customerName(request.getRecipientName())
                .customerMobilePhone(request.getPhoneNumber())
                .finalPrice(12500)
                .build();

        given(authService.getCurrentUser()).willReturn(user);
        given(paymentService.createPayment(anyLong(), any(), any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/auctions/{auctionId}/payments", auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value("ORD-123"))
                .andExpect(jsonPath("$.data.customerEmail").value(user.getEmail()));
    }

    @DisplayName("비회원 사용자는 결제를 요청할 수 없다.")
    @Test
    void shouldThrowUserNotFoundExceptionWhenGuestRequestsPayment() throws Exception {
        // given
        Long auctionId = 1L;
        PaymentCreateRequest request = createPaymentCreateRequest();

        given(authService.getCurrentUser()).willReturn(null);

        // when & then
        mockMvc.perform(post("/api/auctions/{auctionId}/payments", auctionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("결제 실패 처리 성공")
    @Test
    void shouldMarkPaymentAsFailedWhenPaymentFails() throws Exception {
        // given
        TossPaymentFailCallbackRequest request = TossPaymentFailCallbackRequest.builder()
                .orderNumber("ORD-123")
                .build();

        // when & then
        mockMvc.perform(post("/api/payments/fail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("결제 승인 성공")
    @Test
    void shouldMarkPaymentAsPaidWhenPaymentConfirmationReceived() throws Exception {
        // given
        TossPaymentConfirmRequest request = TossPaymentConfirmRequest.builder()
                .orderId("ORD-123")
                .amount(12500)
                .paymentType(PaymentType.CREDIT_CARD)
                .paymentKey("PAY-9999")
                .build();

        TossPaymentConfirmResponse response = TossPaymentConfirmResponse.builder()
                .orderId("ORD-123")
                .approvedAt("2025-06-07T22:00:00")
                .customerEmail("buyer@test.com")
                .customerName("구매자")
                .customerMobilePhone("010-0000-0000")
                .build();

        given(paymentService.confirmPayment(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value("ORD-123"))
                .andExpect(jsonPath("$.data.customerEmail").value("buyer@test.com"));
    }

    @DisplayName("결제 승인 시 외부 오류 발생 시 예외 응답")
    @Test
    void shouldThrowExternalExceptionWhenPaymentConfirmationFails() throws Exception {
        // given
        TossPaymentConfirmRequest request = TossPaymentConfirmRequest.builder()
                .orderId("ORD-123")
                .amount(12500)
                .paymentType(PaymentType.CREDIT_CARD)
                .paymentKey("PAY-9999")
                .build();

        given(paymentService.confirmPayment(any()))
                .willThrow(new PaymentExternalException(ErrorCode.TOSS_PAYMENT_FAILED));

        // when & then
        mockMvc.perform(post("/api/payments/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    private User createMockUser() {
        return User.builder()
                .id(1L)
                .email("buyer@test.com")
                .name("구매자")
                .build();
    }

    private PaymentCreateRequest createPaymentCreateRequest() {
        return PaymentCreateRequest.builder()
                .itemPrice(10000)
                .deliveryFee(2500)
                .recipientName("구매자")
                .phoneNumber("010-0000-0000")
                .addressLine1("서울시")
                .addressLine2("강남구")
                .zipCode("12345")
                .successUrl("http://success")
                .failUrl("http://fail")
                .build();
    }
}
