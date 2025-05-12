package com.samyookgoo.palgoosam.search.dto;

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
}
