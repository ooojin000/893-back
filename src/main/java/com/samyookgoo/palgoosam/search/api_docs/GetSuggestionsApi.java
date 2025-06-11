package com.samyookgoo.palgoosam.search.api_docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(
        summary = "검색 자동완성 추천어 조회",
        description = "입력한 키워드를 기반으로 자동완성 검색어 목록을 반환합니다."
)
@ApiResponse(responseCode = "200", description = "자동완성 목록 조회 성공")
public @interface GetSuggestionsApi {
}
