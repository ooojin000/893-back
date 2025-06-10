package com.samyookgoo.palgoosam.bid.unit_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.domain.BidForHighestPriceProjection;
import com.samyookgoo.palgoosam.bid.domain.BidForMyPageProjection;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.User;
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
@DisplayName("BidRepository 유닛 테스트")
class BidRepositoryTest {

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Auction testAuction1;
    private Auction testAuction2;

    private User bidder;
    private User seller;

    @BeforeEach
    void setUp() {
        // 공통 given
        Category testCategory = createCategory();

        bidder = createUser("bidder@test.com", "입찰자");
        seller = createUser("seller@test.com", "판매자");

        testAuction1 = createAuction(seller, "첫번째 경매", 1000, testCategory);
        testAuction2 = createAuction(seller, "두번째 경매", 2000, testCategory);
    }

    @AfterEach
    void tearDown() {
        bidRepository.deleteAllInBatch();
        auctionRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자의 입찰 내역 중 입찰가가 가장 높은 것을 조회한다.")
    void Given_BidderId_When_RetrieveBids_Then_ReturnBidHistory() {
        //given
        createBid(bidder, testAuction1, 1500, false);
        createBid(bidder, testAuction1, 2000, false);
        createBid(bidder, testAuction2, 2500, false);

        //when
        List<BidForMyPageProjection> result = bidRepository.findAllBidsByUserId(bidder.getId());

        //then
        assertThat(result).hasSize(2);

        BidForMyPageProjection firstBid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstBid.getUserPrice()).isEqualTo(2000);
        assertThat(firstBid.getTitle()).isEqualTo("첫번째 경매");
        assertThat(firstBid.getAuctionId()).isEqualTo(testAuction1.getId());
        assertThat(firstBid.getStatus()).isEqualTo(AuctionStatus.active);
    }

    @Test
    @DisplayName("사용자가 참여한 경매의 최고 입찰가를 조회한다.")
    public void Given_BidderId_When_RetrieveBids_Then_ReturnHighestBid() {
        //given
        createBid(bidder, testAuction1, 1500, false);
        createBid(bidder, testAuction1, 2000, false);
        createBid(bidder, testAuction2, 2500, false);

        User otherBidder = createUser("other@test.com", "다른 입찰자");
        createBid(otherBidder, testAuction2, 5000, false);

        //when
        List<BidForHighestPriceProjection> result = bidRepository.findHighestBidProjectsByBidderId(bidder.getId());

        //then
        assertThat(result).hasSize(2);

        BidForHighestPriceProjection testAuction1Bid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(testAuction1Bid.getAuctionId()).isEqualTo(testAuction1.getId());
        assertThat(testAuction1Bid.getBidHighestPrice()).isEqualTo(2000);

        BidForHighestPriceProjection testAuction2Bid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(testAuction2Bid.getAuctionId()).isEqualTo(testAuction2.getId());
        assertThat(testAuction2Bid.getBidHighestPrice()).isEqualTo(5000);
    }

    @Test
    @Transactional
    @DisplayName("현재 입찰가 조회 중 삭제된 입찰은 조회되지 않는다")
    void findHighestBidProjectsByBidderId_ExcludesDeletedBids() {
        //given
        createBid(bidder, testAuction1, 1500, false);
        createBid(bidder, testAuction1, 2000, false);
        createBid(bidder, testAuction2, 2500, false);

        User otherBidder = createUser("other@test.com", "다른 입찰자");
        Bid deletedBid = createBid(otherBidder, testAuction2, 5000, false);
        deletedBid.cancel();

        //when
        List<BidForHighestPriceProjection> result = bidRepository.findHighestBidProjectsByBidderId(bidder.getId());

        //then
        assertThat(result).hasSize(2);

        BidForHighestPriceProjection testAuction1Bid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(testAuction1Bid.getAuctionId()).isEqualTo(testAuction1.getId());
        assertThat(testAuction1Bid.getBidHighestPrice()).isEqualTo(2000);

        BidForHighestPriceProjection testAuction2Bid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(testAuction2Bid.getAuctionId()).isEqualTo(testAuction2.getId());
        assertThat(testAuction2Bid.getBidHighestPrice()).isEqualTo(2500);

    }

    @Test
    @Transactional
    @DisplayName("사용자의 입찰 내역 조회 중 삭제된 입찰은 조회되지 않는다")
    void findAllBidsByUserId_ExcludesDeletedBids() {
        //given
        createBid(bidder, testAuction1, 1500, false);
        Bid testAuction1DeletedBid = createBid(bidder, testAuction1, 2000, false);
        createBid(bidder, testAuction2, 2500, false);

        testAuction1DeletedBid.cancel();

        //when
        List<BidForMyPageProjection> result = bidRepository.findAllBidsByUserId(bidder.getId());

        //then
        assertThat(result).hasSize(2);

        BidForMyPageProjection firstBid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstBid.getAuctionId()).isEqualTo(testAuction1.getId());
        assertThat(firstBid.getUserPrice()).isEqualTo(1500);

        // 두 번째 경매 입찰 검증
        BidForMyPageProjection secondBid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction2.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(secondBid.getAuctionId()).isEqualTo(testAuction2.getId());
        assertThat(secondBid.getUserPrice()).isEqualTo(2500);

    }

    @Test
    @DisplayName("입찰 내역이 없는 사용자는 빈 목록을 반환한다")
    void findAllBidsByUserId_NoBids_ReturnsEmptyList() {
        // given
        User userWithoutBid = createUser("userWithoutBid@test.com", "userWithoutBid");

        // when
        List<BidForMyPageProjection> result = bidRepository.findAllBidsByUserId(userWithoutBid.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("같은 경매에 여러 입찰 시 가장 높은 금액만 조회된다")
    void findAllBidsByUserId_MultipleBindsOnSameAuction_ReturnsHighestOnly() {
        //given
        createBid(bidder, testAuction1, 10000, false);
        createBid(bidder, testAuction1, 20000, false);
        createBid(bidder, testAuction2, 30000, false);

        //when
        List<BidForMyPageProjection> result = bidRepository.findAllBidsByUserId(bidder.getId());

        //then
        assertThat(result).hasSize(2);

        BidForMyPageProjection firstBid = result.stream()
                .filter(bid -> bid.getAuctionId().equals(testAuction1.getId()))
                .findFirst()
                .orElseThrow();

        assertThat(firstBid.getAuctionId()).isEqualTo(testAuction1.getId());
        assertThat(firstBid.getUserPrice()).isEqualTo(20000);

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

    private Bid createBid(User bidder, Auction auction, int price, boolean isWinning) {
        Bid bid = Bid.builder()
                .bidder(bidder)
                .auction(auction)
                .price(price)
                .isWinning(isWinning)
                .isDeleted(false)
                .build();
        return bidRepository.save(bid);
    }

    private Category createCategory() {
        Category category = Category.builder()
                .name("test")
                .build();
        return categoryRepository.save(category);
    }
}