package com.samyookgoo.palgoosam.auction.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryRequest {
    @NotNull
    private Long id;
    private String mainCategory;
    private String subCategory;
    private String detailCategory;
}
