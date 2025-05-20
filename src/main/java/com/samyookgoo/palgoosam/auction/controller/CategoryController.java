package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.dto.response.CategoryResponseDto;
import com.samyookgoo.palgoosam.auction.service.CategoryService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryResponseDto>>> getCategory() {
        return ResponseEntity.ok(BaseResponse.success("카테고리가 성공적으로 전송되었습니다.", categoryService.getCategory()));
    }
}
