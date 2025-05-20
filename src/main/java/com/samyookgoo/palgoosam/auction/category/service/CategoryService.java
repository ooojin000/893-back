package com.samyookgoo.palgoosam.auction.category.service;

import com.samyookgoo.palgoosam.auction.category.domain.Category;
import com.samyookgoo.palgoosam.auction.category.dto.CategoryResponseDto;
import com.samyookgoo.palgoosam.auction.category.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponseDto> getCategory() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryList.stream().map(category -> {
            CategoryResponseDto responseDto = new CategoryResponseDto();
            responseDto.setId(category.getId());
            responseDto.setName(category.getName());
            responseDto.setChildrenIdList(
                    category.getChildren().stream().map(Category::getId).collect(Collectors.toList()));
            responseDto.setParentId(category.getParent() != null ? category.getParent().getId() : null);
            return responseDto;
        }).toList();
    }
}
