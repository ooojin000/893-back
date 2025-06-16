package com.samyookgoo.palgoosam.auction.repository;

import static com.samyookgoo.palgoosam.auction.constant.AuctionStatus.pending;
import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.projection.AuctionScrapCount;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@DataJpaTest
class ScrapRepositoryTest {

    @Autowired
    private ScrapRepository scrapRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("스크랩 수를 경매별로 집계하여 조회한다")
    @Test
    void testCountGroupedByAuctionIds() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = userRepository.save(createUser("user@test.com", "사용자"));
        Category category = categoryRepository.save(createCategory("가전", null));

        Auction auction1 = auctionRepository.save(
                createAuction(category, user, now.plusHours(3), now.plusHours(4), pending, now));
        Auction auction2 = auctionRepository.save(
                createAuction(category, user, now.plusHours(4), now.plusHours(8), pending, now));

        for (int i = 0; i < 3; i++) {
            scrapRepository.save(createScrap(user, auction1));
        }

        for (int i = 0; i < 2; i++) {
            scrapRepository.save(createScrap(user, auction2));
        }

        // when
        List<AuctionScrapCount> result = scrapRepository.countGroupedByAuctionIds(
                List.of(auction1.getId(), auction2.getId()));

        // then
        Map<Long, Integer> countMap = result.stream()
                .collect(Collectors.toMap(AuctionScrapCount::getAuctionId, c -> c.getScrapCount().intValue()));

        assertThat(countMap.get(auction1.getId())).isEqualTo(3);
        assertThat(countMap.get(auction2.getId())).isEqualTo(2);
    }

    private Category createCategory(String name, Category parent) {
        return categoryRepository.save(
                Category.builder()
                        .name(name)
                        .parent(parent)
                        .build()
        );
    }

    private User createUser(String email, String name) {
        return userRepository.save(
                User.builder()
                        .email(email)
                        .name(name)
                        .profileImage("img.png")
                        .providerId(name)
                        .provider("LOCAL")
                        .build()
        );
    }

    private Auction createAuction(Category category, User seller,
                                  LocalDateTime startTime, LocalDateTime endTime,
                                  AuctionStatus status, LocalDateTime createdAt) {
        return auctionRepository.save(
                Auction.builder()
                        .title("상품 제목")
                        .description("상품 내용")
                        .basePrice(100000)
                        .category(category)
                        .seller(seller)
                        .startTime(startTime)
                        .endTime(endTime)
                        .status(status)
                        .itemCondition(ItemCondition.brand_new)
                        .createdAt(createdAt)
                        .build()
        );
    }

    private Scrap createScrap(User user, Auction auction) {
        return Scrap.builder()
                .user(user)
                .auction(auction)
                .createdAt(LocalDateTime.now())
                .build();
    }

}