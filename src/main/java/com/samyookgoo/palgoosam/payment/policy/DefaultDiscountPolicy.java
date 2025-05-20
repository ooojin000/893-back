package com.samyookgoo.palgoosam.payment.policy;

import org.springframework.stereotype.Component;

@Component
public class DefaultDiscountPolicy implements DeliveryPolicy {
    private static final int FREE_THRESHOLD = 50_000;
    private static final int DELIVERY_FEE = 2_500;

    @Override
    public int calculate(int itemPrice) {
        return itemPrice >= FREE_THRESHOLD ? 0 : DELIVERY_FEE;
    }
}
