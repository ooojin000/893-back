package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DisplayName("SearchHistory 유닛 테스트")
class SearchHistoryEntityTest {

    @Test
    @DisplayName("검색 기록은 검색 카운트를 1회 증가시키고, 논리적인 삭제 상태를 복구할 수 있다.")
    public void Given_SearchHistory_When_CallRestoreAndIncrementMethod_Then_UpdateIsDeleteAndSearchCount() {
        //given
        SearchHistory target = SearchHistory.builder()
                .isDeleted(true)
                .searchCount(1L)
                .build();

        SearchHistory expected = SearchHistory.builder()
                .isDeleted(false)
                .searchCount(2L)
                .build();
        //when
        target.restoreAndIncrement();

        //then
        assertThat(target.getSearchCount()).isEqualTo(expected.getSearchCount());
        assertThat(target.getIsDeleted()).isEqualTo(expected.getIsDeleted());
    }

    @Test
    @DisplayName("검색 기록은 논리적인 삭제 상태로 변경할 수 있다.")
    public void Given_SearchHistory_When_CallsoftDeleteSearchHistoryMethod_Then_UpdateIsDelete() {
        //given
        SearchHistory target = SearchHistory.builder()
                .isDeleted(false)
                .build();

        SearchHistory expected = SearchHistory.builder()
                .isDeleted(true)
                .build();
        //when
        target.softDeleteSearchHistory();

        //then
        assertThat(target.getIsDeleted()).isEqualTo(expected.getIsDeleted());
    }

    @Test
    @DisplayName("유저가 검색 기록에 대한 권한이 없다면 참을 반환한다.")
    public void Given_SearchHistoryAndUserId_When_CallhasPermissionMethod_Then_ReturnTrue() {
        //given
        User user = User.builder()
                .id(1L)
                .build();

        SearchHistory target = SearchHistory.builder()
                .isDeleted(false)
                .user(user)
                .build();

        //when
        Boolean result = target.hasPermission(user.getId());

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유저가 검색 기록에 대한 권한이 없다면 거짓을 반환한다.")
    public void Given_SearchHistoryAndUserId_When_CallhasPermissionMethod_Then_ReturnFalse() {
        //given
        User userWithPermission = User.builder()
                .id(1L)
                .build();

        User userWithoutPermission = User.builder()
                .id(2L)
                .build();

        SearchHistory target = SearchHistory.builder()
                .isDeleted(false)
                .user(userWithPermission)
                .build();

        //when
        Boolean result = target.hasPermission(userWithoutPermission.getId());

        //then
        assertThat(result).isFalse();
    }

}
