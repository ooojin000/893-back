package com.samyookgoo.palgoosam.auction.api_docs.auction;

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
        summary = "경매 상품 상세 조회",
        description = "경매 상품 ID를 기준으로 상세 정보를 조회합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "상세 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "경매 상품을 찾을 수 없음")
})
public @interface AuctionDetailApi {
}
