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
        summary = "검색 기록 삭제",
        description = "사용자의 특정 검색 기록을 삭제합니다."
)
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "검색 기록 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "해당 검색 기록 ID를 찾을 수 없음")
})
public @interface DeleteSearchHistory {
}
