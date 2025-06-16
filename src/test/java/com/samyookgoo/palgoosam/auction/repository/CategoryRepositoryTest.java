package com.samyookgoo.palgoosam.auction.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.auction.domain.Category;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("부모가 있고 자식이 있는 카테고리 조회")
    void testFindByParentIsNotNullAndChildrenIsNotEmpty() {
        // given
        Category mainCategory = categoryRepository.save(createCategory("전자기기", null));
        Category subCategory = categoryRepository.save(createCategory("모바일", mainCategory));
        Category detailCategory = categoryRepository.save(createCategory("스마트폰", subCategory));

        // when
        List<Category> categories = categoryRepository.findByParentIsNotNullAndChildrenIsNotEmpty();

        // then
        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("모바일");
        assertThat(categories.get(0).getChildren()).isNotEmpty();
    }

    private Category createCategory(String name, Category parent) {
        Category category = Category.builder()
                .name(name)
                .parent(parent)
                .build();
        
        if (parent != null) {
            parent.getChildren().add(category);
        }

        return categoryRepository.save(category);
    }
}