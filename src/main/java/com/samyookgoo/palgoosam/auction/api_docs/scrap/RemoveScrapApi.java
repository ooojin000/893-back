package com.samyookgoo.palgoosam.auction.api_docs.scrap;

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
        summary = "스크랩 취소",
        description = "스크랩된 경매 상품을 취소합니다. 스크랩되지 않은 상품일 경우 409(CONFLICT)를 반환합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "스크랩 취소 성공"),
        @ApiResponse(responseCode = "409", description = "스크랩되지 않은 상품입니다.")
})
public @interface RemoveScrapApi {
}
