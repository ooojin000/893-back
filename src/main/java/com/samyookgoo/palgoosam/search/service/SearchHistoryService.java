package com.samyookgoo.palgoosam.search.service;


import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryCreateRequestDto;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryResponseDto;
import com.samyookgoo.palgoosam.search.exception.SearchHistoryBadRequestException;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.exception.UserForbiddenException;
import com.samyookgoo.palgoosam.user.exception.UserUnauthorizedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchHistoryService {
    private final SearchHistoryRepository searchHistoryRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<SearchHistoryResponseDto> getSearchHistory() {
        User user = getAuthenticatedUser(authService.getCurrentUser());

        List<SearchHistory> searchHistoryList = searchHistoryRepository.findAllByUserId(user.getId());

        return searchHistoryList.stream().map(SearchHistoryResponseDto::from).collect(Collectors.toList());
    }

    public List<String> getSearchSuggestionList(String keyword) {
        return searchHistoryRepository.findByFullTextKeyword(keyword).stream().map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteSearchHistory(Long searchHistoryId) {
        User user = getAuthenticatedUser(authService.getCurrentUser());

        SearchHistory target = searchHistoryRepository.findById(searchHistoryId)
                .orElseThrow(() -> new SearchHistoryBadRequestException(ErrorCode.SEARCH_HISTORY_NOT_FOUND));

        if (target.hasPermission(user.getId())) {
            target.softDeleteSearchHistory();
        } else {
            throw new UserForbiddenException();
        }
    }

    @Transactional
    public void recordUserSearch(SearchHistoryCreateRequestDto requestDto) {
        User user = getAuthenticatedUser(authService.getCurrentUser());

        String keyword = requestDto.getKeyword().trim();

        validateKeyword(keyword);

        Optional<SearchHistory> existingSearch = searchHistoryRepository.findByKeywordAndUserId(keyword,
                user.getId());

        if (existingSearch.isPresent()) {
            existingSearch.get().restoreAndIncrement();
        } else {
            SearchHistory newSearch = SearchHistory.builder()
                    .keyword(keyword)
                    .isDeleted(false)
                    .user(user)
                    .searchCount(1L)
                    .build();
            searchHistoryRepository.save(newSearch);
        }
    }

    public User getAuthenticatedUser(User user) {
        if (user == null) {
            throw new UserUnauthorizedException();
        }
        return user;
    }

    public void validateKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new SearchHistoryBadRequestException(ErrorCode.SEARCH_HISTORY_BAD_REQUEST);
        }
    }
}
