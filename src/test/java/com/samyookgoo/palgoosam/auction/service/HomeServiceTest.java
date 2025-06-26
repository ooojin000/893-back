package com.samyookgoo.palgoosam.auction.service;

import static com.samyookgoo.palgoosam.auction.constant.AuctionStatus.active;
import static com.samyookgoo.palgoosam.auction.constant.AuctionStatus.completed;
import static com.samyookgoo.palgoosam.auction.constant.AuctionStatus.pending;
import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.dto.home.ActiveRankingResponse;
import com.samyookgoo.palgoosam.auction.dto.home.DashboardResponse;
import com.samyookgoo.palgoosam.auction.dto.home.PendingRankingResponse;
import com.samyookgoo.palgoosam.auction.dto.home.RecentAuctionResponse;
import com.samyookgoo.palgoosam.auction.dto.home.SubCategoryBestItemResponse;
import com.samyookgoo.palgoosam.auction.dto.home.TopBidResponse;
import com.samyookgoo.palgoosam.auction.dto.home.UpcomingAuctionResponse;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class HomeServiceTest {

    @Autowired
    private BidRepository bidRepository;
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private AuctionImageRepository auctionImageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ScrapRepository scrapRepository;
    @Autowired
    private HomeService homeService;

    private LocalDateTime now;
    private Category category;
    private User seller;
    private User buyer;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        category = createCategory("전자기기", null);
        seller = createUser("seller@test.com", "판매자");
        buyer = createUser("buyer@test.com", "구매자");
    }

    @AfterEach
    void tearDown() {
        bidRepository.deleteAllInBatch();
        scrapRepository.deleteAllInBatch();
        auctionImageRepository.deleteAllInBatch();
        auctionRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @DisplayName("총 이용자 수, 총 등록된 경매 수, 현재 진행중인 경매 수를 조회한다")
    @Test
    void testGetUserAndAuctionCounts() {
        // given
        auctionRepository.save(
                createAuction(category, seller, now.minusHours(1), now.plusHours(1), active, now.minusHours(2)));
        auctionRepository.save(
                createAuction(category, buyer, now.minusHours(2), now.plusHours(2), active, now.minusHours(2)));
        auctionRepository.save(
                createAuction(category, seller, now.minusHours(2), now.minusHours(1), completed, now.minusHours(2)));

        // when
        DashboardResponse result = homeService.getDashboard();

        // then
        assertThat(result.getTotalUserCount()).isEqualTo(2);
        assertThat(result.getTotalAuctionCount()).isEqualTo(3);
        assertThat(result.getActiveAuctionCount()).isEqualTo(2);
    }

    @DisplayName("최근 등록된 경매 6개를 조회한다")
    @Test
    void testGetRecentAuctionsLimitedToSix() {
        // given
        for (int i = 0; i < 7; i++) {
            Auction auction = createAuction(category, seller, now.minusMinutes(i + 1), now.plusHours(1),
                    active, now.minusMinutes(i));
            auctionRepository.save(auction);
            auctionImageRepository.save(createAuctionImage(auction, "image" + i));
        }

        // when
        List<RecentAuctionResponse> result = homeService.getRecentAuctions();

        // then
        assertThat(result).hasSizeLessThanOrEqualTo(6);
        assertThat(result.get(0).getThumbnailUrl()).isEqualTo("image6");
        assertThat(result.get(0).getAuctionId()).isNotNull();
        assertThat(result.get(0).getStatus()).isEqualTo(active);
    }

    @DisplayName("시작 예정 경매 3건을 응답 형태로 조회하고 스크랩 수와 남은 시간도 포함한다")
    @Test
    void testGetUpcomingAuctions() {
        // given
        List<Auction> auctions = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Auction auction = createAuction(category, seller,
                    now.plusMinutes(10 + i), now.plusHours(2),
                    pending, now.minusHours(1));
            auctionRepository.save(auction);
            auctionImageRepository.save(createAuctionImage(auction, "image" + i));
            auctions.add(auction);
        }

        scrapRepository.save(createScrap(buyer, auctions.get(0)));
        scrapRepository.save(createScrap(buyer, auctions.get(0)));
        scrapRepository.save(createScrap(buyer, auctions.get(1)));
        scrapRepository.save(createScrap(buyer, auctions.get(1)));

        // when
        List<UpcomingAuctionResponse> result = homeService.getUpcomingAuctions();

        // then
        assertThat(result).hasSizeLessThanOrEqualTo(3);
        assertThat(result.get(0).getScrapCount()).isEqualTo(2); // startTime 빠름
        assertThat(result.get(1).getScrapCount()).isEqualTo(2);
        assertThat(result.get(2).getScrapCount()).isEqualTo(0);
        assertThat(result.get(0).getLeftTime()).matches("\\d{2}:\\d{2}:\\d{2}");
    }

    @DisplayName("최근 7일간 낙찰가 기준 최고 입찰 5개를 조회한다")
    @Test
    void testGetTopBid() {
        // given
        List<Auction> auctions = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Auction auction = createAuction(category, seller,
                    now.minusDays(2), now.minusDays(1),
                    AuctionStatus.completed, now.minusDays(3));
            auctionRepository.save(auction);
            auctionImageRepository.save(createAuctionImage(auction, "image" + i));
            auctions.add(auction);

            for (int j = 0; j <= i; j++) {
                bidRepository.save(createBid(auction, buyer, 1000 + j * 100, j == i));
            }
        }

        // when
        List<TopBidResponse> result = homeService.getTopBid();

        // then
        assertThat(result).hasSizeLessThanOrEqualTo(5);
        assertThat(result.get(0).getItemPrice()).isGreaterThanOrEqualTo(result.get(1).getItemPrice());
        assertThat(result.get(0).getBuyer()).contains("구*자");
        assertThat(result.get(0).getBidCount()).isGreaterThan(0);
        assertThat(result.get(0).getRankNum()).isEqualTo(1);
    }

    @DisplayName("경매 입찰 수 기준으로 TOP 8 경매를 조회하고 순위 정보를 반환한다")
    @Test
    void testGetActiveRanking() {
        // given
        List<Auction> auctions = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            Auction auction = createAuction(category, seller,
                    now.minusHours(2), now.plusHours(2),
                    AuctionStatus.active, now.minusHours(3));
            auctionRepository.save(auction);
            auctionImageRepository.save(createAuctionImage(auction, "thumb" + i));
            auctions.add(auction);

            for (int j = 0; j <= i; j++) {
                bidRepository.save(createBid(auction, buyer, 1000 + j * 100, j == i));
            }
        }

        // when
        List<ActiveRankingResponse> result = homeService.getActiveRanking();

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSizeLessThanOrEqualTo(8);

        // 입찰 수 기준 내림차순 정렬, 동일할 경우 auctionId 오름차순
        for (int i = 0; i < result.size() - 1; i++) {
            ActiveRankingResponse current = result.get(i);
            ActiveRankingResponse next = result.get(i + 1);

            boolean isSorted = current.getBidCount() > next.getBidCount()
                    || (current.getBidCount() == next.getBidCount()
                    && current.getAuctionId() < next.getAuctionId());

            assertThat(isSorted).isTrue();
        }

        assertThat(result.get(0).getThumbnailUrl()).contains("thumb");
        assertThat(result.get(0).getRankNum()).isEqualTo(1);
        assertThat(result.get(result.size() - 1).getRankNum()).isEqualTo(result.size());
    }

    @Test
    @DisplayName("스크랩 수 기반 경매 예정인 상품 랭킹을 조회한다.")
    void getPendingRankingTest() {
        // given
        for (int i = 0; i < 5; i++) {
            Auction auction = auctionRepository.save(
                    createAuction(category, seller, now.plusHours(i), now.plusHours(i + 2), pending,
                            now.minusHours(i)));

            for (int j = 0; j <= i; j++) {
                scrapRepository.save(createScrap(buyer, auction));
            }
        }

        // when
        List<PendingRankingResponse> result = homeService.getPendingRanking();

        // then
        assertThat(result).hasSizeLessThanOrEqualTo(8);
        assertThat(result.get(0).getScrapCount()).isGreaterThanOrEqualTo(result.get(1).getScrapCount());
        result.forEach(r -> {
            assertThat(r.getAuctionId()).isNotNull();
            assertThat(r.getTitle()).isNotBlank();
            assertThat(r.getScrapCount()).isNotNull();
        });
    }

    @DisplayName("중분류 카테고리별 스크랩 수 기준 상위 50개 상품 조회한다.")
    @Test
    void testGetSubCategoryBestItem() {
        // given
        Category subCategory = createCategory("컴퓨터", category);
        for (int i = 0; i < 5; i++) {
            Auction auction = createAuction(subCategory, seller, now.minusDays(i), now.plusDays(i),
                    active, now.minusDays(i));
            auctionRepository.save(auction);
            auctionImageRepository.save(createAuctionImage(auction, "image" + i));

            for (int j = 0; j <= i; j++) {
                scrapRepository.save(createScrap(buyer, auction));
            }
        }

        // when
        List<SubCategoryBestItemResponse> result = homeService.getSubCategoryBestItem();

        // then
        assertThat(result.size()).isLessThanOrEqualTo(50);
        result.forEach(response -> {
            assertThat(response.getSubCategoryId()).isEqualTo(subCategory.getId());
            assertThat(response.getSubCategoryName()).isEqualTo(subCategory.getName());
            assertThat(response.getItems()).hasSize(5);
        });
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

    private AuctionImage createAuctionImage(Auction auction, String url) {
        return auctionImageRepository.save(
                AuctionImage.builder()
                        .auction(auction)
                        .url(url)
                        .originalName("origin" + url)
                        .storeName("store" + url)
                        .imageSeq(0)
                        .isDeleted(false)
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

    private Bid createBid(Auction auction, User bidder, int price, boolean isWinning) {
        return Bid.builder()
                .auction(auction)
                .price(price)
                .bidder(bidder)
                .isWinning(isWinning)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
