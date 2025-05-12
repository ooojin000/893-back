package com.samyookgoo.palgoosam.search.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchHistoryCreateRequestDto {

    @NotBlank
    String keyword;
}
