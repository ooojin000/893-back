package com.samyookgoo.palgoosam.search.controller;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryCreateRequestDto;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryResponseDto;
import com.samyookgoo.palgoosam.search.service.SearchHistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchHistoryController {
    private final SearchHistoryService searchHistoryService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<SearchHistoryResponseDto>>> getSearchHistory() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("검색 기록을 정상적으로 조회했습니다.", searchHistoryService.getSearchHistory()));
    }

    @PostMapping
    public ResponseEntity<BaseResponse> recordUserSearch(
            @RequestBody SearchHistoryCreateRequestDto requestDto) {
        searchHistoryService.recordUserSearch(requestDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.success("검색 기록을 정상적으로 저장했습니다.", null));
    }
}
