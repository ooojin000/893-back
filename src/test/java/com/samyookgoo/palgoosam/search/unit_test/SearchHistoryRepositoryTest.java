package com.samyookgoo.palgoosam.search.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.search.domain.SearchHistory;
import com.samyookgoo.palgoosam.search.repository.SearchHistoryRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("SearchHistoryRepository 유닛 테스트")
class SearchHistoryRepositoryTest {

    @Autowired
    private SearchHistoryRepository searchHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    @DisplayName("SearchHistory를 저장한다.")
    public void Given_SearchHistory_When_Save_Then_NormallySave() throws Exception {
        //given
        User testUser = createTestUser("tester", "test@test.com");
        SearchHistory searchHistory = createSearchHistory("test", testUser);

        //when
        SearchHistory saved = searchHistoryRepository.save(searchHistory);

        //then
        assertThat(saved.getSearchCount()).isEqualTo(searchHistory.getSearchCount());
        assertThat(saved.getUser()).isEqualTo(testUser);
        assertThat(saved.getIsDeleted()).isEqualTo(searchHistory.getIsDeleted());
        assertThat(saved.getKeyword()).isEqualTo(searchHistory.getKeyword());
    }

    @Test
    @DisplayName("findByAllUserId를 호출하면 10개 이하의 개수를 보여준다.")
    public void Given_UserId_When_CallfindAllByUserIdMethod_Then_ReturnListLengthUnder10() {
        //given
        User testUser = createTestUser("tester2", "test2@test.com");
        List<SearchHistory> searchHistoryList = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            SearchHistory searchHistory = createSearchHistory("test" + i, testUser);
            searchHistoryList.add(searchHistory);
        }
        searchHistoryRepository.saveAll(searchHistoryList);

        //when
        List<SearchHistory> foundSearchHistoryList = searchHistoryRepository.findAllByUserId(testUser.getId());

        //then
        assertThat(foundSearchHistoryList.size()).isEqualTo(10);

        assertThat(foundSearchHistoryList)
                .extracting(SearchHistory::getKeyword)
                .containsExactly("test10", "test9", "test8", "test7", "test6", "test5", "test4", "test3", "test2",
                        "test1");
    }

    @Test
    @DisplayName("findByKeywordAndUserId를 호출하면 Optional<SearchHistory>를 반환한다.")
    public void Given_KeywordAndUserId_When_CallfindByKeywordAndUserId_Then_ReturnOptionalSearchHistory() {
        //given
        User testUser = createTestUser("tester", "test@test.com");
        String existedKeyword = "test1";
        SearchHistory searchHistory = createSearchHistory(existedKeyword, testUser);
        String notExistedKeyword = "test";

        searchHistoryRepository.save(searchHistory);

        //when
        Optional<SearchHistory> existingSearch = searchHistoryRepository.findByKeywordAndUserId(existedKeyword,
                testUser.getId());
        Optional<SearchHistory> nullSearch = searchHistoryRepository.findByKeywordAndUserId(notExistedKeyword,
                testUser.getId());

        //then
        assertThat(existingSearch.get().getKeyword()).isEqualTo(existedKeyword);
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
        return SearchHistory.builder()
                .keyword(keyword)
                .isDeleted(false)
                .user(user)
                .searchCount(1L)
                .build();
    }


}
