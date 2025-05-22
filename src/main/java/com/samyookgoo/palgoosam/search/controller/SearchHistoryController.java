package com.samyookgoo.palgoosam.search.controller;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.search.api_docs.DeleteSearchHistory;
import com.samyookgoo.palgoosam.search.api_docs.GetSearchHistoryApi;
import com.samyookgoo.palgoosam.search.api_docs.GetSuggestionsApi;
import com.samyookgoo.palgoosam.search.api_docs.RecordUserSearchApi;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryCreateRequestDto;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryResponseDto;
import com.samyookgoo.palgoosam.search.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Parameter;
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

    @GetSearchHistoryApi
    @GetMapping
    public ResponseEntity<BaseResponse<List<SearchHistoryResponseDto>>> getSearchHistory() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("검색 기록을 정상적으로 조회했습니다.", searchHistoryService.getSearchHistory()));
    }

    @RecordUserSearchApi
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

    @GetSuggestionsApi
    @GetMapping("/suggestions")
    public ResponseEntity<BaseResponse> getSuggestions(
            @Parameter(name = "keyword", description = "사용자가 입력 중인 검색어", required = true)
            @RequestParam String keyword
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("자동 완성 목록을 정상적으로 조회했습니다.",
                        searchHistoryService.getSearchSuggestionList(keyword)));
    }

    @DeleteSearchHistory
    @DeleteMapping("/{searchHistoryId}")
    public ResponseEntity<BaseResponse> deleteSearchHistory(
            @Parameter(name = "searchHistoryId", description = "삭제할 검색 기록 ID", required = true)
            @PathVariable Long searchHistoryId
    ) {
        searchHistoryService.deleteSearchHistory(searchHistoryId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("검색 기록을 삭제했습니다.", null));
    }

}
