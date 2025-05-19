package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.dto.request.CategoryRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "카테고리 응답 DTO")
public class CategoryResponse {

    @Schema(description = "카테고리 ID", example = "12")
    private Long id;

    @Schema(description = "대분류 카테고리 이름", example = "전자기기")
    private String mainCategory;

    @Schema(description = "중분류 카테고리 이름", example = "모바일")
    private String subCategory;

    @Schema(description = "소분류 카테고리 이름", example = "스마트폰")
    private String detailCategory;

    public static CategoryResponse from(Category category) {
        List<String> names = new ArrayList<>();
        Category originalCategory = category;

        while (category != null) {
            names.add(0, category.getName());
            category = category.getParent();
        }

        return CategoryResponse.builder()
                .id(originalCategory != null ? originalCategory.getId() : null)
                .mainCategory(names.size() > 0 ? names.get(0) : null)
                .subCategory(names.size() > 1 ? names.get(1) : null)
                .detailCategory(names.size() > 2 ? names.get(2) : null)
                .build();
    }

    public static CategoryResponse from(Category category, CategoryRequest request) {
        return CategoryResponse.builder()
                .id(category != null ? category.getId() : null)
                .mainCategory(request.getMainCategory())
                .subCategory(request.getSubCategory())
                .detailCategory(request.getDetailCategory())
                .build();
    }
}