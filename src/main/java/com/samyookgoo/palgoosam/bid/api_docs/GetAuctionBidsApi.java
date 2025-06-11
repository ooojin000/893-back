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
        summary = "경매 입찰 내역 조회",
        description = "경매 ID를 기준으로 해당 경매의 모든 입찰 내역을 조회합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "입찰 내역 조회 성공"),
        @ApiResponse(responseCode = "404", description = "해당 경매를 찾을 수 없음")
})
public @interface GetAuctionBidsApi {
}
