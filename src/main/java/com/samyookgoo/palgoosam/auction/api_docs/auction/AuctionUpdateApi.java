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
        summary = "경매 상품 수정",
        description = "경매 상품 정보를 수정합니다. 이미지도 함께 수정할 수 있으며, 기존 이미지를 대체하거나 유지할 수 있습니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "400", description = "이미지 혹은 요청 데이터 오류"),
        @ApiResponse(responseCode = "404", description = "경매 상품을 찾을 수 없음")
})
public @interface AuctionUpdateApi {
}
