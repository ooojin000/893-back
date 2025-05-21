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
        summary = "연관 경매 상품 조회",
        description = "현재 경매 상품과 같은 소분류 또는 중분류 카테고리에 속한 경매 중 상품 10개 이내를 추천합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "연관 상품 조회 성공"),
        @ApiResponse(responseCode = "404", description = "기준 경매 상품을 찾을 수 없음")
})
public @interface RelatedAuctionGetApi {
}
