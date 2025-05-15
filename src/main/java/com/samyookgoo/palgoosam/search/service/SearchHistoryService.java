package com.samyookgoo.palgoosam.search.service;


import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryCreateRequestDto;
import com.samyookgoo.palgoosam.search.dto.SearchHistoryResponseDto;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.ArrayList;
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
    private final AuthService authService;

    public List<SearchHistoryResponseDto> getSearchHistory() {

        User user = authService.getCurrentUser();

        if (user == null) {
            return new ArrayList<>();
        }

        List<SearchHistory> searchHistoryList = searchHistoryRepository.findAllByUserId(user.getId());

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
        User user = authService.getCurrentUser();
        if (user == null) {
            return;
        }
        Optional<SearchHistory> processed = processSearchKeyword(requestDto, user);
        processed.ifPresent(searchHistoryRepository::save);
    }


    private Optional<SearchHistory> processSearchKeyword(SearchHistoryCreateRequestDto requestDto, User user) {
        String keyword = requestDto.getKeyword().trim();
        if (keyword.isEmpty()) {
            return Optional.empty();
        }

        SearchHistory existingSearch = searchHistoryRepository.findByKeywordAndUserId(keyword, user.getId());

        if (existingSearch != null) {
            if (existingSearch.getIsDeleted()) {
                existingSearch.setIsDeleted(false);
            }
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
        return searchHistoryRepository.findByFullTextKeyword(keyword).stream().map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    public void deleteSearchHistory(Long searchHistoryId) {
        User user = authService.getCurrentUser();
        if (user == null) {
            return;
        }
        SearchHistory target = searchHistoryRepository.findById(searchHistoryId)
                .orElseThrow(() -> new EntityNotFoundException("SearchHistory not found"));
        target.setIsDeleted(true);
        if (target.getUser().getId().equals(user.getId())) {
            searchHistoryRepository.save(target);
        }
    }
}
