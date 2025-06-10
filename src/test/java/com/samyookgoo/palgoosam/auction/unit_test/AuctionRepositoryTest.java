package com.samyookgoo.palgoosam.auction.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionForMyPageProjection;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.domain.BidForHighestPriceProjection;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("AuctionRepository 유닛 테스트")
class AuctionRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionImageRepository auctionImageRepository;

    @Autowired
    private ScrapRepository scrapRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    private User seller;
    private User scraper;
    private User bidder;

    private final Integer auctionCount = 3;

    @BeforeEach
    void setUp() {
        // 카테고리 생성
        testCategory = createCategory();

        // 사용자 생성
        seller = createUser("seller@test.com", "판매자");
        scraper = createUser("scraper@test.com", "스크랩 사용자");
        bidder = createUser("bidder@test.com", "입찰자");
    }

    @AfterEach
    void tearDown() {
        scrapRepository.deleteAllInBatch();
        bidRepository.deleteAllInBatch();
        auctionRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자가 등록한 모든 경매를 조회한다.")
    void findAllAuctionProjectionBySellerId_ValidSeller_ReturnsAuctions() {
        //given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionBySellerId(seller.getId());

        //then
        assertThat(result).hasSize(3);

        // 첫 번째 경매
        AuctionForMyPageProjection firstAuction = result.stream()
                .filter(auction -> auction.getAuctionId().equals(auction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstAuction.getAuctionId()).isEqualTo(auction1.getId());
        assertThat(firstAuction.getTitle()).isEqualTo("첫번째 경매");

        // 두 번째 경매
        AuctionForMyPageProjection secondAuction = result.stream()
                .filter(auction -> auction.getAuctionId().equals(auction2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(secondAuction.getAuctionId()).isEqualTo(auction2.getId());
        assertThat(secondAuction.getTitle()).isEqualTo("두번째 경매");

        // 세 번째 경매
        AuctionForMyPageProjection thirdAuction = result.stream()
                .filter(auction -> auction.getAuctionId().equals(auction3.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(thirdAuction.getAuctionId()).isEqualTo(auction3.getId());
        assertThat(thirdAuction.getTitle()).isEqualTo("세번째 경매");
    }


    @Test
    @DisplayName("사용자가 스크랩한 모든 경매를 조회한다.")
    void findAllAuctionProjectionWithScrapByUserId_ValidUser_ReturnsScrapedAuctions() {
        //given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        createScrap(scraper, auction1);
        createScrap(scraper, auction2);
        createScrap(scraper, auction3);

        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionWithScrapByUserId(
                scraper.getId());

        //then
        assertThat(result).hasSize(3);

        // 첫 번째 경매
        AuctionForMyPageProjection firstAuction = result.stream()
                .filter(auction -> auction.getAuctionId().equals(auction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstAuction.getAuctionId()).isEqualTo(auction1.getId());
        assertThat(firstAuction.getTitle()).isEqualTo("첫번째 경매");

        // 두 번째 경매
        AuctionForMyPageProjection secondAuction = result.stream()
                .filter(auction -> auction.getAuctionId().equals(auction2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(secondAuction.getAuctionId()).isEqualTo(auction2.getId());
        assertThat(secondAuction.getTitle()).isEqualTo("두번째 경매");

        // 세 번째 경매
        AuctionForMyPageProjection thirdAuction = result.stream()
                .filter(auction -> auction.getAuctionId().equals(auction3.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(thirdAuction.getAuctionId()).isEqualTo(auction3.getId());
        assertThat(thirdAuction.getTitle()).isEqualTo("세번째 경매");
    }

    @Test
    @DisplayName("사용자가 등록한 경매가 없다면 아무 것도 없는 빈 값을 조회한다.")
    void findAllAuctionProjectionBySellerId_NoAuctions_ReturnsEmptyList() {
        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionBySellerId(scraper.getId());

        //then
        assertThat(result).hasSize(0);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자가 스크랩한 경매가 없다면 아무 것도 없는 빈 값을 조회한다.")
    void findAllAuctionProjectionWithScrapByUserId_NoScraps_ReturnsEmptyList() {
        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionWithScrapByUserId(
                seller.getId());

        //then
        assertThat(result).hasSize(0);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자가 등록한 경매의 최고 입찰가를 조회한다.")
    public void Given_Seller_When_RetrieveBids_Then_ReturnHighestBid() {
        // given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        createBid(bidder, auction1, 1500, false); // 첫번째 경매에 1500원 입찰
        createBid(bidder, auction1, 2000, false); // 첫번째 경매에 2000원 입찰
        createBid(bidder, auction2, 2500, false); // 두번째 경매에 2500원 입찰

        // 다른 사용자의 입찰 (최고가 테스트용)
        User otherBidder = createUser("other@test.com", "다른 입찰자");
        createBid(otherBidder, auction1, 2200, false); // 첫번째 경매에 더 높은 입찰

        //when
        List<BidForHighestPriceProjection> result = auctionRepository.findHighestBidProjectsBySellerId(seller.getId());

        //then
        assertThat(result).hasSize(auctionCount);

        BidForHighestPriceProjection firstBid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(auction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstBid.getAuctionId()).isEqualTo(auction1.getId());
        assertThat(firstBid.getBidHighestPrice()).isEqualTo(2200);

        // 두 번째 경매 입찰 검증
        BidForHighestPriceProjection secondBid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(auction2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(secondBid.getAuctionId()).isEqualTo(auction2.getId());
        assertThat(secondBid.getBidHighestPrice()).isEqualTo(2500);
    }

    @Test
    @DisplayName("사용자가 등록한 경매의 입찰이 없으면 0을 반환한다.")
    public void Given_Seller_When_RetrieveBids_Then_ReturnZero() {
        //given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        createBid(bidder, auction1, 1500, false); // 첫번째 경매에 1500원 입찰
        createBid(bidder, auction1, 2000, false); // 첫번째 경매에 2000원 입찰
        createBid(bidder, auction2, 2500, false); // 두번째 경매에 2500원 입찰

        //when
        List<BidForHighestPriceProjection> result = auctionRepository.findHighestBidProjectsBySellerId(seller.getId());

        //then
        assertThat(result).hasSize(auctionCount);

        BidForHighestPriceProjection firstBid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(auction3.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstBid.getAuctionId()).isEqualTo(auction3.getId());
        assertThat(firstBid.getBidHighestPrice()).isEqualTo(0);
    }

    @Test
    @Transactional
    @DisplayName("사용자가 스크랩한 경매 중 삭제된 경매는 조회되지 않는다")
    void findAllAuctionProjectionWithScrapByUserId_ExcludesDeletedAuctions() {
        //given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        createScrap(scraper, auction1);
        createScrap(scraper, auction2);
        createScrap(scraper, auction3);

        auction1.setIsDeleted(true);

        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionWithScrapByUserId(
                scraper.getId());

        //then
        assertThat(result).hasSize(auctionCount - 1);
    }

    @Test
    @DisplayName("스크랩한 경매 중 만료된 경매도 조회된다")
    void findAllAuctionProjectionWithScrapByUserId_IncludesExpiredAuctions() {
        //given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        createScrap(scraper, auction1);
        createScrap(scraper, auction2);
        createScrap(scraper, auction3);
        auction1.setStatus(AuctionStatus.completed);

        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionWithScrapByUserId(
                scraper.getId());

        //then
        assertThat(result).hasSize(auctionCount);
    }

    @Test
    @DisplayName("사용자가 등록한 경매 중 삭제된 경매는 조회되지 않는다")
    void findAllAuctionProjectionBySellerId_ExcludesDeletedAuctions() {
        //given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        auction1.setIsDeleted(true);

        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionBySellerId(
                seller.getId());

        //then
        assertThat(result).hasSize(auctionCount);
    }

    @Test
    @Transactional
    @DisplayName("사용자가 등록한 경매 중 만료된 경매도 조회된다")
    void findAllAuctionProjectionBySellerId_IncludesExpiredAuctions() {
        //given
        Auction auction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        Auction auction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
        Auction auction3 = createAuction(seller, "세번째 경매", 3000, testCategory);

        auction1.setStatus(AuctionStatus.completed);

        //when
        List<AuctionForMyPageProjection> result = auctionRepository.findAllAuctionProjectionBySellerId(
                seller.getId());

        //then
        assertThat(result).hasSize(auctionCount);
    }


    private User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .profileImage(email + name)
                .providerId(name)
                .provider("LOCAL")
                .build();
        return userRepository.save(user);
    }

    private Auction createAuction(User seller, String title, int basePrice, Category category) {
        Auction auction = Auction.builder()
                .seller(seller)
                .title(title)
                .description("경매 설명")
                .basePrice(basePrice)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now().plusHours(1))
                .status(AuctionStatus.active)
                .category(category)
                .itemCondition(ItemCondition.damaged)
                .build();
        return auctionRepository.save(auction);
    }

    private void createScrap(User scraper, Auction auction) {
        Scrap scrap = Scrap.builder()
                .user(scraper)
                .auction(auction)
                .build();
        scrapRepository.save(scrap);
    }

    private void createBid(User bidder, Auction auction, int price, boolean isWinning) {
        Bid bid = Bid.builder()
                .bidder(bidder)
                .auction(auction)
                .price(price)
                .isWinning(isWinning)
                .isDeleted(false)
                .build();
        bidRepository.save(bid);
    }

    private Category createCategory() {
        Category category = Category.builder()
                .name("testCategory")
                .build();
        return categoryRepository.save(category);
    }
}