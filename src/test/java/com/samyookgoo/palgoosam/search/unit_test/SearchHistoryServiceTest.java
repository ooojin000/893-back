package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.search.exception.SearchHistoryBadRequestException;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.search.service.SearchHistoryService;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.exception.UserUnauthorizedException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SearchHistoryService 유닛 테스트")
class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private SearchHistoryService searchHistoryService;

    // getAuthenticatedUser 유닛 테스트
    @Test
    @DisplayName("User 객체가 Null이 아니면 입력받은 user를 반환한다.")
    void Given_User_When_UserNotNull_Then_ReturnUser() {
        // Given
        User user = new User();

        // When
        User returnedUser = searchHistoryService.getAuthenticatedUser(user);

        // Then

        assertThat(returnedUser).isEqualTo(user);
    }

    @Test
    @DisplayName("User 객체가 Null이면 UserUnauthorized 예외를 던진다.")
    public void Given_User_When_UserIsNull_Then_ThrowUserUnauthorizedException() {
        //given
        User user = null;

        //when
        UserUnauthorizedException e = Assertions.assertThrows(UserUnauthorizedException.class,
                () -> searchHistoryService.getAuthenticatedUser(user));

        //then
        assertThat(e.getErrorCode()).isEqualTo(ErrorCode.UNAUTHORIZED_USER);
    }

    // validateKeyword 유닛 테스트
    @Test
    @DisplayName("keyword가 비어있을 때 예외를 던진다.")
    public void Given_Keyword_When_KeywordIsEmpty_Then_ThrowBadRequestException() {
        //given
        String emptyKeyword = "";

        //when
        SearchHistoryBadRequestException exceptionWithEmptyKeyword = Assertions.assertThrows(
                SearchHistoryBadRequestException.class, () -> searchHistoryService.validateKeyword(emptyKeyword));

        //then
        assertThat(exceptionWithEmptyKeyword.getErrorCode()).isEqualTo(ErrorCode.SEARCH_HISTORY_BAD_REQUEST);
    }

    @Test
    @DisplayName("keyword가 Null일 때 예외를 던진다.")
    public void Given_Keyword_When_KeywordIsNull_Then_ThrowBadRequestException() {
        //given
        String nullKeyword = null;

        //when
        SearchHistoryBadRequestException exceptionWithNullKeyword = Assertions.assertThrows(
                SearchHistoryBadRequestException.class, () -> searchHistoryService.validateKeyword(nullKeyword));

        //then
        assertThat(exceptionWithNullKeyword.getErrorCode()).isEqualTo(ErrorCode.SEARCH_HISTORY_BAD_REQUEST);
    }
}