package com.samyookgoo.palgoosam.bid.controller.request;

import com.samyookgoo.palgoosam.common.validation.DivisibleBy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BidRequest {
    @NotNull(message = "입찰 금액은 필수입니다.")
    @Min(value = 0, message = "입찰 금액은 0원 이상이어야 합니다.")
    @Max(value = 1_000_000_000, message = "입찰 금액은 최대 10억까지 가능합니다.")
    @DivisibleBy(value = 100, message = "입찰 금액은 100원 단위여야 합니다.")
    private Integer price;
}
