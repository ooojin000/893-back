package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.search.exception.SearchHistoryBadRequestException;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.search.service.SearchHistoryService;
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

    @InjectMocks
    private SearchHistoryService searchHistoryService;

    // validateKeyword 유닛 테스트
    @Test
    @DisplayName("keyword가 비어있을 때 예외를 던진다.")
    public void validateKeyword_KeywordIsEmpty_ThrowBadRequestException() {
        //given
        String emptyKeyword = "";

        //when
        Throwable thrown = catchThrowable(() -> searchHistoryService.validateKeyword(emptyKeyword));

        //then
        assertThat(thrown).isInstanceOf(SearchHistoryBadRequestException.class)
                .hasMessage(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST);
    }

    @Test
    @DisplayName("keyword가 Null일 때 예외를 던진다.")
    public void validateKeyword_KeywordIsNull_ThrowBadRequestException() {
        //given
        String nullKeyword = null;

        //when
        Throwable thrown = catchThrowable(() -> searchHistoryService.validateKeyword(nullKeyword));

        //then
        assertThat(thrown).isInstanceOf(SearchHistoryBadRequestException.class)
                .hasMessage(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST);
    }

    @Test
    @DisplayName("keyword가 공백일 때 예외를 던진다.")
    public void validateKeyword_WithWhiteSpaceOnly_ThrowBadRequestException() {
        //given
        String whiteSpaceKeyword1 = " ";
        String whiteSpaceKeyword2 = "\t";
        String whiteSpaceKeyword3 = "\r";

        //when
        Throwable exceptionWithWhiteSpaceKeyword1 = catchThrowable(
                () -> searchHistoryService.validateKeyword(whiteSpaceKeyword1));
        Throwable exceptionWithWhiteSpaceKeyword2 = catchThrowable(
                () -> searchHistoryService.validateKeyword(whiteSpaceKeyword2));
        Throwable exceptionWithWhiteSpaceKeyword3 = catchThrowable(
                () -> searchHistoryService.validateKeyword(whiteSpaceKeyword3));

        //then
        assertThat(exceptionWithWhiteSpaceKeyword1).isInstanceOf(SearchHistoryBadRequestException.class)
                .hasMessage(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST);

        assertThat(exceptionWithWhiteSpaceKeyword2).isInstanceOf(SearchHistoryBadRequestException.class)
                .hasMessage(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST);

        assertThat(exceptionWithWhiteSpaceKeyword3).isInstanceOf(SearchHistoryBadRequestException.class)
                .hasMessage(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST.getMessage())
                .extracting("errorCode")
                .isEqualTo(ErrorCode.SEARCH_HISTORY_BLANK_BAD_REQUEST);
    }
}