package com.samyookgoo.palgoosam.search.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SearchHistoryCreateRequestDto {

    @NotBlank
    String keyword;
}
