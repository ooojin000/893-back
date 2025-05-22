package com.samyookgoo.palgoosam.search.api_docs;

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
        summary = "검색 기록 저장",
        description = "사용자가 입력한 검색어를 검색 기록에 저장합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 기록 저장 성공"),
        @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않음")
})
public @interface RecordUserSearchApi {
}
