package com.samyookgoo.palgoosam.auction.dto.response;

import com.samyookgoo.palgoosam.auction.domain.Category;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String mainCategory;
    private String subCategory;
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
}