package com.samyookgoo.palgoosam.payment.policy;

import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.payment.exception.PaymentBadRequestException;

public interface DeliveryPolicy {
    int calculate(int itemPrice);

    default void validateDeliveryFee(int itemPrice, int deliveryFee) {
        int expectedFee = calculate(itemPrice);
        if (deliveryFee != expectedFee) {
            throw new PaymentBadRequestException(ErrorCode.INVALID_DELIVERY_FEE);
        }
    }
}
