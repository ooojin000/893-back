package com.samyookgoo.palgoosam.auction.api_docs.auction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "경매 상품 검색",
        description = "검색 조건(키워드, 카테고리, 정렬 등)에 따라 경매 상품을 조회합니다."
)
@ApiResponse(responseCode = "200", description = "검색 결과 조회 성공")
public @interface AuctionSearchApi {
}
