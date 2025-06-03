package com.samyookgoo.palgoosam.search.dto;

import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchHistoryResponseDto {
    private Long id;
    private String keyword;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SearchHistoryResponseDto from(SearchHistory searchHistory) {
        return SearchHistoryResponseDto.builder()
                .id(searchHistory.getId())
                .keyword(searchHistory.getKeyword())
                .createdAt(searchHistory.getCreatedAt())
                .updatedAt(searchHistory.getUpdatedAt())
                .build();
    }
}
