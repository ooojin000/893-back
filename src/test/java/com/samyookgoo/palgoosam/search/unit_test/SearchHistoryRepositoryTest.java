package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("SearchHistoryRepository 유닛 테스트")
class SearchHistoryRepositoryTest {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = createTestUser("currentUser", "currentUser@test.com");
    }

    @AfterEach
    void tearDown() {
        searchHistoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("회원인 사용자의 검색 내역을 저장한다.")
    public void save_ValidSearchHistory_SavesSuccessfully() {
        //given
        SearchHistory searchHistory = createSearchHistory("test", currentUser);

        //when
        SearchHistory saved = searchHistoryRepository.save(searchHistory);

        //then
        assertThat(saved.getSearchCount()).isEqualTo(searchHistory.getSearchCount());
        assertThat(saved.getUser().getId()).isEqualTo(currentUser.getId());
        assertThat(saved.getIsDeleted()).isEqualTo(searchHistory.getIsDeleted());
        assertThat(saved.getKeyword()).isEqualTo(searchHistory.getKeyword());
    }

    @Test
    @DisplayName("회원은 10개 이하의 검색 기록을 볼 수 있다.")
    public void findAllByUserId_OverTenRecords_ReturnsLatestTen() {
        //given
        List<SearchHistory> searchHistoryList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            SearchHistory searchHistory = createSearchHistory("test" + i, currentUser);
            searchHistoryList.add(searchHistory);
        }

        //when
        List<SearchHistory> foundSearchHistoryList = searchHistoryRepository.findAllByUserId(currentUser.getId());

        //then
        assertThat(foundSearchHistoryList.size()).isEqualTo(10);

        assertThat(foundSearchHistoryList)
                .extracting(SearchHistory::getKeyword)
                .containsExactly("test10", "test9", "test8", "test7", "test6", "test5", "test4", "test3", "test2",
                        "test1");
    }

    @Test
    @DisplayName("사용자가 검색어를 입력하면 기존 검색 기록을 확인할 수 있다.")
    public void findByKeywordAndUserId_ExistingKeyword_ReturnsSearchHistory() {
        //given
        String existedKeyword = "test1";
        SearchHistory searchHistory = createSearchHistory(existedKeyword, currentUser);

        searchHistoryRepository.save(searchHistory);

        //when
        SearchHistory existingSearch = searchHistoryRepository.findByKeywordAndUserId(existedKeyword,
                currentUser.getId()).orElseThrow();

        //then
        assertThat(existingSearch.getKeyword()).isEqualTo(existedKeyword);
    }

    @Test
    @DisplayName("기존 검색 기록이 없다면 아무 것도 확인할 수 없다.")
    public void findByKeywordAndUserId_NonExistingKeyword_ReturnsEmpty() {
        //given
        String notExistedKeyword = "test";

        //when
        Optional<SearchHistory> nullSearch = searchHistoryRepository.findByKeywordAndUserId(notExistedKeyword,
                currentUser.getId());

        //then
        assertThat(nullSearch.isEmpty()).isTrue();
    }

    private User createTestUser(String name, String email) {
        return userRepository.save(User.builder()
                .name(name)
                .email(email)
                .profileImage("test@testCdn.com")
                .provider("google")
                .providerId("google-test-provider-id-" + System.nanoTime()) // 유니크한 ID
                .build());
    }

    private SearchHistory createSearchHistory(String keyword, User user) {
        SearchHistory searchHistory = SearchHistory.builder()
                .keyword(keyword)
                .isDeleted(false)
                .user(user)
                .searchCount(1L)
                .build();

        return searchHistoryRepository.save(searchHistory);
    }


}
