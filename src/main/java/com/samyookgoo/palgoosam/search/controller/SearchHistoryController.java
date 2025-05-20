package com.samyookgoo.palgoosam.search.controller;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryCreateRequestDto;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryResponseDto;
import com.samyookgoo.palgoosam.search.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
@Tag(name = "검색 기록", description = "사용자 검색 기록 및 자동완성 API")
public class SearchHistoryController {
    private final SearchHistoryService searchHistoryService;

    @Operation(
            summary = "검색 기록 조회",
            description = "현재 로그인한 사용자의 검색 기록 목록을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "검색 기록 조회 성공")
    @GetMapping
    public ResponseEntity<BaseResponse<List<SearchHistoryResponseDto>>> getSearchHistory() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("검색 기록을 정상적으로 조회했습니다.", searchHistoryService.getSearchHistory()));
    }

    @Operation(
            summary = "검색 기록 저장",
            description = "사용자가 입력한 검색어를 검색 기록에 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 기록 저장 성공"),
            @ApiResponse(responseCode = "400", description = "입력 값이 유효하지 않음")
    })
    @PostMapping
    public ResponseEntity<BaseResponse> recordUserSearch(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "검색 기록 저장 요청 정보", required = true)
            @RequestBody SearchHistoryCreateRequestDto requestDto
    ) {
        searchHistoryService.recordUserSearch(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("검색 기록을 정상적으로 저장했습니다.", null));
    }

    @Operation(
            summary = "검색 자동완성 추천어 조회",
            description = "입력한 키워드를 기반으로 자동완성 검색어 목록을 반환합니다."
    )
    @ApiResponse(responseCode = "200", description = "자동완성 목록 조회 성공")
    @GetMapping("/suggestions")
    public ResponseEntity<BaseResponse> getSuggestions(
            @Parameter(name = "keyword", description = "사용자가 입력 중인 검색어", required = true)
            @RequestParam String keyword
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("자동 완성 목록을 정상적으로 조회했습니다.",
                        searchHistoryService.getSearchSuggestionList(keyword)));
    }

    @Operation(
            summary = "검색 기록 삭제",
            description = "사용자의 특정 검색 기록을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 기록 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 검색 기록 ID를 찾을 수 없음")
    })
    @DeleteMapping("/{searchHistoryId}")
    public ResponseEntity<BaseResponse> deleteSearchHistory(
            @Parameter(name = "searchHistoryId", description = "삭제할 검색 기록 ID", required = true)
            @PathVariable Long searchHistoryId
    ) {
        searchHistoryService.deleteSearchHistory(searchHistoryId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("검색 기록을 삭제했습니다.", null));
    }

}
