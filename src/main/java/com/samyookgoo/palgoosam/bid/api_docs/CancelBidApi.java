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
        summary = "입찰 취소",
        description = "사용자가 본인의 입찰을 취소합니다. SSE를 통해 취소 결과가 실시간으로 반영됩니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "입찰 취소 성공"),
        @ApiResponse(responseCode = "404", description = "유저, 경매, 또는 입찰 정보를 찾을 수 없음"),
        @ApiResponse(responseCode = "403", description = "다른 사용자의 입찰은 취소할 수 없음")
})
public @interface CancelBidApi {
}
