package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SearchHistory 유닛 테스트")
class SearchHistoryEntityTest {

    @Test
    @DisplayName("restoreAndIncrement() 메서드를 호출하면 SearchHistory의 IsDeleted는 false로, SearchCount는 1만큼 더해져서 갱신된다.")
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
    @DisplayName("softDeleteSearchHistory() 메서드를 호출하면 SearchHistory의 IsDeleted는 true로 갱신된다.")
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
    @DisplayName("현재 유저가 SearchHistory에 대한 권한이 있을 때, hasPermission() 메서드를 호출하면 참을 반환한다.")
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
    @DisplayName("현재 유저가 SearchHistory에 대한 권한이 없을 때, hasPermission() 메서드를 호출하면 거짓을 반환한다.")
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
