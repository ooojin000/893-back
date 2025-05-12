package com.samyookgoo.palgoosam.search.service;


import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryCreateRequestDto;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryResponseDto;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchHistoryService {
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    public List<SearchHistoryResponseDto> getSearchHistory() {
        /*
        userService에서 사용자 판별하는 부분 추가
        if (user != null) 아래 로직 동작하도록
         */
        User dummyUser = userRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<SearchHistory> searchHistoryList = searchHistoryRepository.findAllByUserId(dummyUser.getId());

        return searchHistoryList.stream().map(searchHistory ->
                SearchHistoryResponseDto.builder()
                        .id(searchHistory.getId())
                        .keyword(searchHistory.getKeyword())
                        .createdAt(searchHistory.getCreatedAt())
                        .updatedAt(searchHistory.getUpdatedAt())
                        .build()
        ).collect(Collectors.toList());
    }

    public void recordUserSearch(SearchHistoryCreateRequestDto requestDto) {
        /*
        userService에서 사용자 판별하는 부분 추가
        if (user != null) 아래 로직 동작하도록
         */
        User dummyUser = userRepository.findById(1L).orElseThrow(() -> new EntityNotFoundException("User not found"));
        Optional<SearchHistory> processed = processSearchKeyword(requestDto, dummyUser);
        processed.ifPresent(searchHistoryRepository::save);
    }


    private Optional<SearchHistory> processSearchKeyword(SearchHistoryCreateRequestDto requestDto, User user) {
        String keyword = requestDto.getKeyword().trim();
        if (keyword.isEmpty()) {
            return Optional.empty();
        }

        SearchHistory existingSearch = searchHistoryRepository.findByKeywordAndUserAndIsDeleted(keyword, user.getId(),
                false);

        if (existingSearch != null) {
            existingSearch.incrementSearchCount();
            return Optional.of(existingSearch);
        }

        return Optional.of(SearchHistory.builder()
                .keyword(keyword)
                .isDeleted(false)
                .user(user)
                .searchCount(1L)
                .build());
    }

    public List<String> getSearchSuggestionList(String keyword) {
        return searchHistoryRepository.findAllByKeyword(keyword).stream().map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    public void deleteSearchHistory(Long searchHistoryId) {
        /*
        userService에서 사용자 판별하는 부분 추가
        if (user != null) 아래 로직 동작하도록
         */
        SearchHistory target = searchHistoryRepository.findById(searchHistoryId)
                .orElseThrow(() -> new EntityNotFoundException("SearchHistory not found"));
        target.setIsDeleted(true);
        if (target.getUser().getId().equals(1L)) {
            searchHistoryRepository.save(target);
        }
    }
}
