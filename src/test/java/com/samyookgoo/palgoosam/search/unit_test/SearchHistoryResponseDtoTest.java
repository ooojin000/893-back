package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryResponseDto;
import com.samyookgoo.palgoosam.user.domain.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SearchHistoryResponseDto 유닛 테스트")
class SearchHistoryResponseDtoTest {

    @Test
    @DisplayName("SearchHistoryResponseDto는 엔티티를 기반으로 해당 DTO를 생성할 수 있다.")
    public void Given_SearchHistory_When_CallFromMethod_Then_CreateSearchHistoryResponseDto() {
        //given
        LocalDateTime now = LocalDateTime.now();
        User user = new User();
        SearchHistory target = SearchHistory.builder()
                .id(1L)
                .user(user)
                .createdAt(now)
                .updatedAt(now)
                .isDeleted(false)
                .searchCount(1L)
                .keyword("test")
                .build();

        SearchHistoryResponseDto expected = SearchHistoryResponseDto.builder()
                .id(1L)
                .keyword("test")
                .createdAt(now)
                .updatedAt(now)
                .build();

        //when
        SearchHistoryResponseDto createdDto = SearchHistoryResponseDto.from(target);

        //then
        assertThat(createdDto.getId()).isEqualTo(expected.getId());
        assertThat(createdDto.getKeyword()).isEqualTo(expected.getKeyword());
        assertThat(createdDto.getCreatedAt()).isEqualTo(expected.getCreatedAt());
        assertThat(createdDto.getUpdatedAt()).isEqualTo(expected.getUpdatedAt());
    }

}
