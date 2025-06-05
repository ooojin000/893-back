package com.samyookgoo.palgoosam.bid.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.bid.controller.response.BidResultResponse;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.exception.BidBadRequestException;
import com.samyookgoo.palgoosam.bid.exception.BidForbiddenException;
import com.samyookgoo.palgoosam.bid.exception.BidInvalidStateException;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class BidServiceTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    AuctionImageRepository auctionImageRepository;

    @Autowired
    private SseService sseService;

    @Autowired
    private BidService bidService;

    @AfterEach
    void tearDown() {
        bidRepository.deleteAllInBatch();
        auctionImageRepository.deleteAllInBatch();
        auctionRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("(판매자가 아닌) 일반 회원이 입찰하면 성공한다.")
    @Test
    void regularUserCanPlaceBid() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when
        BidResultResponse bidResultResponse = bidService.placeBid(auction.getId(), user, 1500);

        //then
        assertThat(bidResultResponse).isNotNull();
        assertThat(bidResultResponse.getBid()).isNotNull();
        assertThat(bidResultResponse.getBid())
                .extracting("bidderEmail", "bidPrice")
                .contains(user.getEmail(), 1500);

    }

    @DisplayName("판매자가 자신의 경매에 입찰하면 예외가 발생한다.")
    @Test
    void sellerCannotPlaceBidOnOwnAuction() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        //when & then
        assertThatThrownBy(() -> bidService.placeBid(auction.getId(), auction.getSeller(), 1500))
                .isInstanceOf(BidForbiddenException.class)
                .hasMessage("판매자는 자신의 경매에 입찰할 수 없습니다.");
    }

    @DisplayName("입찰가가 최고가보다 클 경우 입찰에 성공 한다.")
    @Test
    void shouldSucceedWhenBidPriceIsHighest() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user1 = createUser("user1@test.com", "유저1");
        User user2 = createUser("user2@test.com", "유저2");
        userRepository.save(user1);
        userRepository.save(user2);

        Bid bid = createBid(auction, user1, 1500, true, false, LocalDateTime.now());
        bidRepository.save(bid);

        //when
        BidResultResponse bidResultResponse = bidService.placeBid(auction.getId(), user2, 2000);

        //then
        assertThat(bidResultResponse).isNotNull();
        assertThat(bidResultResponse.getBid()).isNotNull();
        assertThat(bidResultResponse.getBid())
                .extracting("bidderEmail", "bidPrice")
                .contains(user2.getEmail(), 2000);
    }

    @DisplayName("입찰가가 최고가 일 경우 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenBidPriceIsHighest() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user1 = createUser("user1@test.com", "유저1");
        User user2 = createUser("user2@test.com", "유저2");
        userRepository.save(user1);
        userRepository.save(user2);

        Bid bid = createBid(auction, user1, 1500, true, false, LocalDateTime.now());
        bidRepository.save(bid);

        //when & then
        assertThatThrownBy(() -> bidService.placeBid(auction.getId(), user2, 1500))
                .isInstanceOf(BidBadRequestException.class)
                .hasMessage("현재 최고가보다 높은 금액을 입력해야 합니다.");
    }

    @DisplayName("입찰가가 최고가보다 작을 경우 예외가 발생한다.")
    @Test
    void shouldThrowExceptionWhenBidPriceIsNotHighest() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user1 = createUser("user1@test.com", "유저1");
        User user2 = createUser("user2@test.com", "유저2");
        userRepository.save(user1);
        userRepository.save(user2);

        Bid bid = createBid(auction, user1, 1500, true, false, LocalDateTime.now());
        bidRepository.save(bid);

        //when & then
        assertThatThrownBy(() -> bidService.placeBid(auction.getId(), user2, 1400))
                .isInstanceOf(BidBadRequestException.class)
                .hasMessage("현재 최고가보다 높은 금액을 입력해야 합니다.");
    }

    @DisplayName("입찰가가 시작가보다 클 경우 입찰에 성공 한다.")
    @Test
    void shouldSucceedWhenBidPriceIsHigherThanStartPrice() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when
        BidResultResponse bidResultResponse = bidService.placeBid(auction.getId(), user, 2000);

        //then
        assertThat(bidResultResponse).isNotNull();
        assertThat(bidResultResponse.getBid()).isNotNull();
        assertThat(bidResultResponse.getBid())
                .extracting("bidderEmail", "bidPrice")
                .contains(user.getEmail(), 2000);
    }

    @DisplayName("입찰가가 시작가 일 경우 입찰에 성공한다.")
    @Test
    void shouldThrowWhenBidPriceEqualsStartPrice() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when
        BidResultResponse bidResultResponse = bidService.placeBid(auction.getId(), user,
                auction.getBasePrice());

        //then
        assertThat(bidResultResponse).isNotNull();
        assertThat(bidResultResponse.getBid()).isNotNull();
        assertThat(bidResultResponse.getBid())
                .extracting("bidderEmail", "bidPrice")
                .contains(user.getEmail(), auction.getBasePrice());
    }

    @DisplayName("입찰가가 시작가보다 작을 경우 예외가 발생한다.")
    @Test
    void shouldThrowWhenBidPriceIsLowerThanStartPrice() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when & then
        assertThatThrownBy(() -> bidService.placeBid(auction.getId(), user, 900))
                .isInstanceOf(BidBadRequestException.class)
                .hasMessage("시작가보다 높은 금액을 입력해야 합니다.");
    }

    @DisplayName("입찰가가 10억보다 작을 경우 입찰에 성공한다.")
    @Test
    void shouldThrowWhenBidPriceIsLowerThanTenBillion() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(999_000_000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when
        BidResultResponse bidResultResponse = bidService.placeBid(auction.getId(), user, 999_999_000);

        //then
        assertThat(bidResultResponse).isNotNull();
        assertThat(bidResultResponse.getBid()).isNotNull();
        assertThat(bidResultResponse.getBid())
                .extracting("bidderEmail", "bidPrice")
                .contains(user.getEmail(), 999_999_000);
    }

    @DisplayName("입찰가가 10억 일 경우 입찰에 성공한다.")
    @Test
    void shouldSucceedWhenBidPriceEqualsTenBillion() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(999_000_000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when
        BidResultResponse bidResultResponse = bidService.placeBid(auction.getId(), user, 1_000_000_000);

        //then
        assertThat(bidResultResponse).isNotNull();
        assertThat(bidResultResponse.getBid()).isNotNull();
        assertThat(bidResultResponse.getBid())
                .extracting("bidderEmail", "bidPrice")
                .contains(user.getEmail(), 1_000_000_000);
    }

    @DisplayName("입찰가가 10억보다 클 경우 예외가 발생한다.")
    @Test
    void shouldSucceedWhenBidPriceIsGreaterThanTenBillion() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(999_000_000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when & then
        assertThatThrownBy(() -> bidService.placeBid(auction.getId(), user, 1_000_000_100))
                .isInstanceOf(BidBadRequestException.class)
                .hasMessage("입찰 금액은 최대 10억까지 가능합니다.");
    }

    @DisplayName("경매 중 입찰 시 입찰이 성공한다.")
    @Test
    void shouldSucceedWhenBiddingDuringAuction() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when
        BidResultResponse bidResultResponse = bidService.placeBid(auction.getId(), user, 1500);

        //then
        assertThat(bidResultResponse).isNotNull();
        assertThat(bidResultResponse.getBid()).isNotNull();
        assertThat(bidResultResponse.getBid())
                .extracting("bidderEmail", "bidPrice")
                .contains(user.getEmail(), 1500);
    }

    @DisplayName("경매 종료 후 입찰 시 예외가 발생한다.")
    @Test
    void shouldFailBidAfterAuctionEnds() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now.minusHours(2), now.minusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when & then
        assertThatThrownBy(() -> bidService.placeBid(auction.getId(), user, 1500))
                .isInstanceOf(BidInvalidStateException.class)
                .hasMessage("현재는 입찰 가능한 시간이 아닙니다.");
    }

    @DisplayName("경매 시작 전 입찰 시 예외가 발생한다.")
    @Test
    void shouldFailBidBeforeAuctionStarts() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now.plusHours(1), now.plusHours(2));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        //when & then
        assertThatThrownBy(() -> bidService.placeBid(auction.getId(), user, 1500))
                .isInstanceOf(BidInvalidStateException.class)
                .hasMessage("현재는 입찰 가능한 시간이 아닙니다.");
    }

    @DisplayName("입찰자가 본인의 입찰을 취소하면 성공한다.")
    @Test
    void shouldCancelBidSuccessfully() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user = createUser("user@test.com", "유저");
        userRepository.save(user);

        Bid bid = createBid(auction, user, 1500, true, false, LocalDateTime.now());
        bidRepository.save(bid);

        //when
        bidService.cancelBid(auction.getId(), bid.getId(), user.getId());

        //then
        Bid cancelledBid = bidRepository.findById(bid.getId()).orElseThrow();
        assertThat(cancelledBid.isCancelled()).isTrue();
        assertThat(cancelledBid.getIsWinning()).isFalse();
    }

    @DisplayName("입찰자가 아닌 사용자가 입찰 취소할 경우 예외가 발생한다.")
    @Test
    void shouldThrowWhenUserIsNotBidder() {
        LocalDateTime now = LocalDateTime.now();
        //given
        Auction auction = createAuctionWithDependencies(1000, now, now.plusHours(1));
        AuctionImage auctionImage = createAuctionImage(auction, "img.jpg");
        auctionRepository.save(auction);
        auctionImageRepository.save(auctionImage);

        User user1 = createUser("user1@test.com", "유저1");
        User user2 = createUser("user2@test.com", "유저2");
        userRepository.save(user1);
        userRepository.save(user2);

        Bid bid = createBid(auction, user1, 1500, true, false, LocalDateTime.now());
        bidRepository.save(bid);

        //when & then
        assertThatThrownBy(() -> bidService.cancelBid(auction.getId(), bid.getId(), user2.getId()))
                .isInstanceOf(BidForbiddenException.class)
                .hasMessage("본인의 입찰만 취소할 수 있습니다.");
    }

    private Auction createAuctionWithDependencies(int basePrice, LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        Category testCategory = categoryRepository.save(createCategory("test category"));
        User user = userRepository.save(createUser("seller@test.com", "판매자"));
        return createAuction(
                "test auction",
                basePrice,
                testCategory,
                user,
                startTime,
                endTime
        );
    }

    private static Category createCategory(String name) {
        return Category.builder()
                .name(name)
                .build();
    }


    private User createUser(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .profileImage(email + name)
                .providerId(name)
                .provider("LOCAL")
                .build();

    }

    public Auction createAuction(String title,
                                 Integer basePrice,
                                 Category category,
                                 User seller,
                                 LocalDateTime startTime,
                                 LocalDateTime endTime
    ) {

        return Auction.builder()
                .title(title)
                .description("팔아요!")
                .basePrice(basePrice)
                .itemCondition(ItemCondition.brand_new)
                .startTime(startTime)
                .endTime(endTime)
                .category(category)
                .seller(seller)
                .status(AuctionStatus.pending)
                .build();
    }


    private AuctionImage createAuctionImage(Auction auction, String url) {
        return AuctionImage.builder()
                .auction(auction)
                .url(url)
                .originalName("test-original-" + url)
                .storeName("test-store-" + url)
                .imageSeq(0)
                .isDeleted(false)
                .build();
    }


    private Bid createBid(Auction auction, User bidder, int price, boolean isWinning, boolean isDeleted,
                          LocalDateTime createdAt) {
        return Bid.builder()
                .bidder(bidder)
                .auction(auction)
                .price(price)
                .isWinning(isWinning)
                .createdAt(createdAt)
                .isDeleted(isDeleted)
                .build();
    }

}
