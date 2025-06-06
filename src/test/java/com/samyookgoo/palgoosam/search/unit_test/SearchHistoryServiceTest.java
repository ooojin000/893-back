package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.search.exception.SearchHistoryBadRequestException;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.search.service.SearchHistoryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("SearchHistoryService 유닛 테스트")
class SearchHistoryServiceTest {

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private SearchHistoryService searchHistoryService;

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