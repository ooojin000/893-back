package com.samyookgoo.palgoosam.auction.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseDto {
    private Long id;
    private String name;
    private Long parentId;
    private List<Long> childrenIdList;
}
