package com.samyookgoo.palgoosam.bid.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "경매 입찰 요청",
        description = "사용자가 특정 경매에 대해 입찰합니다. 입찰가는 현재 입찰가보다 높아야 하며, SSE로 실시간 반영됩니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "입찰 성공"),
        @ApiResponse(responseCode = "400", description = "입찰가가 기준보다 낮거나 잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "유저 또는 경매를 찾을 수 없음")
})
public @interface PlaceBidApi {
}
