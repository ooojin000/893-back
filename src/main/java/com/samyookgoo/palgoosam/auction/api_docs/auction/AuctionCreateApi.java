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
        summary = "경매 상품 등록",
        description = "경매 상품 정보를 등록하고, 최소 1개에서 최대 10개의 이미지를 업로드합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "201", description = "경매 등록 성공"),
        @ApiResponse(responseCode = "400", description = "이미지 개수 제한 위반 또는 유효하지 않은 요청")
})
public @interface AuctionCreateApi {
}
