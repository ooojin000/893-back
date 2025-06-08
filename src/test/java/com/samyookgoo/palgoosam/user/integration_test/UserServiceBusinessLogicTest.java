package com.samyookgoo.palgoosam.user.integration_test;


import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.payment.constant.PaymentStatus;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.dto.UserAuctionsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserBidsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserInfoResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserPaymentsResponseDto;
import com.samyookgoo.palgoosam.user.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestEntityManager
@Transactional
@DisplayName("UserService 비즈니스 로직 테스트")
class UserServiceBusinessLogicTest {

    @Autowired
    private UserService userService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User currentUser;

    @BeforeEach
    void setUp() {
        currentUser = createUser("currentUser@test.com", "currentUser");
    }

    @Test
    @DisplayName("로그인 사용자를 기반으로 사용자 정보를 조회할 수 있다.")
    public void getUserInfo_ValidUser_ReturnUserInfo() {
        //given
        String email = "currentUser@test.com";
        String name = "currentUser";
        String provider = "LOCAL";
        UserInfoResponseDto expected = new UserInfoResponseDto(email, name, email + name, provider);

        //when
        UserInfoResponseDto userInfoResponseDto = userService.getUserInfo(currentUser);

        //then
        Assertions.assertThat(userInfoResponseDto.getEmail()).isEqualTo(expected.getEmail());
        Assertions.assertThat(userInfoResponseDto.getName()).isEqualTo(expected.getName());
        Assertions.assertThat(userInfoResponseDto.getProvider()).isEqualTo(expected.getProvider());
        Assertions.assertThat(userInfoResponseDto.getProfileUrl()).isEqualTo(expected.getProfileUrl());
    }

    @Test
    @DisplayName("사용자는 여러 경매에 대한 입찰 내역을 조회할 수 있다.")
    public void getUserBids_SeveralAuctions_ReturnBids() {
        //given
        Category testCategory = createCategory();

        String email = "seller@test.com";
        String name = "seller";

        User seller = createUser(email, name);

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, seller);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, seller);

        Bid auction1Bid1 = createBid(currentUser, createdAuction1, 1000, false);
        Bid auction1Bid2 = createBid(currentUser, createdAuction1, 1100, false);
        Bid auction2Bid1 = createBid(currentUser, createdAuction2, 1000, false);

        //when
        List<UserBidsResponseDto> result = userService.getUserBids(currentUser);
        UserBidsResponseDto first = result.get(0);
        UserBidsResponseDto second = result.get(1);

        //then
        Assertions.assertThat(result).hasSize(2);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdAuction1.getId());
        Assertions.assertThat(first.getUserPrice()).isEqualTo(auction1Bid2.getPrice());
        Assertions.assertThat(first.getTitle()).isEqualTo(createdAuction1.getTitle());

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdAuction2.getId());
        Assertions.assertThat(second.getUserPrice()).isEqualTo(auction2Bid1.getPrice());
        Assertions.assertThat(second.getTitle()).isEqualTo(createdAuction2.getTitle());
    }

    @Test
    @DisplayName("사용자의 입찰 내역 중 현재 입찰가를 정상적으로 조회할 수 있다.")
    public void getUserBids_MultipleAuctions_MapsHighestBidsCorrectly() {
        //given
        Category testCategory = createCategory();

        String email = "seller@test.com";
        String name = "seller";

        User seller = createUser(email, name);

        String bidderEmail = "bidder@test.com";
        String bidderName = "bidder";

        User bidder = createUser(bidderEmail, bidderName);

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, seller);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, seller);

        Bid auction1Bid1 = createBid(currentUser, createdAuction1, 1000, false);
        Bid auction1Bid2 = createBid(currentUser, createdAuction1, 1100, false);
        Bid auction2Bid1 = createBid(currentUser, createdAuction2, 1000, false);
        Bid auction2Bid2s = createBid(bidder, createdAuction2, 2000, false);

        //when
        List<UserBidsResponseDto> result = userService.getUserBids(currentUser);
        UserBidsResponseDto first = result.get(0);
        UserBidsResponseDto second = result.get(1);

        //then
        Assertions.assertThat(result).hasSize(2);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdAuction1.getId());
        Assertions.assertThat(first.getUserPrice()).isEqualTo(auction1Bid2.getPrice());
        Assertions.assertThat(first.getBidHighestPrice()).isEqualTo(auction1Bid2.getPrice());
        Assertions.assertThat(first.getTitle()).isEqualTo(createdAuction1.getTitle());

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdAuction2.getId());
        Assertions.assertThat(second.getUserPrice()).isEqualTo(auction2Bid1.getPrice());
        Assertions.assertThat(second.getBidHighestPrice()).isEqualTo(auction2Bid2s.getPrice());
        Assertions.assertThat(second.getTitle()).isEqualTo(createdAuction2.getTitle());
    }

    @Test
    @DisplayName("사용자의 입찰 내역이 없으면 빈 리스트를 조회한다.")
    public void getUserBids_NeverPlaceBid_ReturnEmptyBidList() {
        //given
        Category testCategory = createCategory();

        String email = "seller@test.com";
        String name = "seller";
        User seller = createUser(email, name);

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, seller);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, seller);

        //when
        List<UserBidsResponseDto> result = userService.getUserBids(currentUser);

        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자는 판매 등록한 경매를 조회할 수 있다.")
    public void getUserAuctions_SeveralAuctions_ReturnAuctions() {
        //given
        Category testCategory = createCategory();

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, currentUser);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, currentUser);

        //when
        List<UserAuctionsResponseDto> result = userService.getUserAuctions(currentUser);

        //then
        Assertions.assertThat(result).hasSize(2);

        UserAuctionsResponseDto first = result.get(0);
        UserAuctionsResponseDto second = result.get(1);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdAuction1.getId());
        Assertions.assertThat(first.getTitle()).isEqualTo(createdAuction1.getTitle());

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdAuction2.getId());
        Assertions.assertThat(second.getTitle()).isEqualTo(createdAuction2.getTitle());
    }

    @Test
    @DisplayName("사용자가 등록한 경매에 입찰 내역이 없다면 현재 입찰가를 0원으로 조회한다.")
    public void getUserAuctions_AuctionsWithoutBids_ReturnsHighestBidAsZero() {
        //given
        Category testCategory = createCategory();

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, currentUser);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, currentUser);

        //when
        List<UserAuctionsResponseDto> result = userService.getUserAuctions(currentUser);

        //then
        Assertions.assertThat(result).hasSize(2);

        UserAuctionsResponseDto first = result.get(0);
        UserAuctionsResponseDto second = result.get(1);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdAuction1.getId());
        Assertions.assertThat(first.getBidHighestPrice()).isEqualTo(0);

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdAuction2.getId());
        Assertions.assertThat(second.getBidHighestPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 등록한 경매에 입찰 내역이 있다면 삭제되지 않은 가장 높은 입찰 내역을 기준으로 조회한다.")
    public void getUserAuctions_AuctionsWithBids_ReturnsHighestNonDeletedBid() {
        //given
        Category testCategory = createCategory();

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, currentUser);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, currentUser);

        String bidderEmail = "bidder@test.com";
        String bidderName = "bidder";

        User bidder = createUser(bidderEmail, bidderName);

        Bid auction1Bid1 = createBid(bidder, createdAuction1, 1000, false);
        Bid auction1Bid2 = createBid(bidder, createdAuction1, 1100, false);
        Bid auction2Bid1 = createBid(bidder, createdAuction2, 1000, false);
        Bid auction2Bid2 = createBid(bidder, createdAuction2, 2000, false);

        auction2Bid2.cancel();

        //when
        List<UserAuctionsResponseDto> result = userService.getUserAuctions(currentUser);

        //then
        Assertions.assertThat(result).hasSize(2);

        UserAuctionsResponseDto first = result.get(0);
        UserAuctionsResponseDto second = result.get(1);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdAuction1.getId());
        Assertions.assertThat(first.getBidHighestPrice()).isEqualTo(auction1Bid2.getPrice());

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdAuction2.getId());
        Assertions.assertThat(second.getBidHighestPrice()).isEqualTo(auction2Bid1.getPrice());
    }

    @Test
    @DisplayName("사용자가 등록한 경매가 없으면 빈 리스트를 조회한다.")
    public void getUserAuctions_NeverRegisterAuction_ReturnEmptyBidList() {
        //when
        List<UserAuctionsResponseDto> result = userService.getUserAuctions(currentUser);

        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자는 스크랩(찜)한 경매를 조회할 수 있다.")
    public void getUserScraps_SeveralAuctions_ReturnAuctions() {
        //given
        Category testCategory = createCategory();

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, currentUser);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, currentUser);

        Scrap createdScrap1 = createScrap(currentUser, createdAuction1);
        Scrap createdScrap2 = createScrap(currentUser, createdAuction2);

        //when
        List<UserAuctionsResponseDto> result = userService.getUserScraps(currentUser);

        //then
        Assertions.assertThat(result).hasSize(2);

        UserAuctionsResponseDto first = result.get(0);
        UserAuctionsResponseDto second = result.get(1);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdScrap1.getAuction().getId());
        Assertions.assertThat(first.getTitle()).isEqualTo(createdScrap1.getAuction().getTitle());

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdScrap2.getAuction().getId());
        Assertions.assertThat(second.getTitle()).isEqualTo(createdScrap2.getAuction().getTitle());
    }

    @Test
    @DisplayName("사용자가 스크랩(찜)한 경매의 입찰 내역이 없으면 현재 입찰가는 0원이다.")
    public void getUserScraps_ScrapedAuctionsWithoutBids_ReturnsHighestBidAsZero() {
        //given
        Category testCategory = createCategory();

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, currentUser);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, currentUser);

        Scrap createdScrap1 = createScrap(currentUser, createdAuction1);
        Scrap createdScrap2 = createScrap(currentUser, createdAuction2);

        //when
        List<UserAuctionsResponseDto> result = userService.getUserScraps(currentUser);

        //then
        Assertions.assertThat(result).hasSize(2);

        UserAuctionsResponseDto first = result.get(0);
        UserAuctionsResponseDto second = result.get(1);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdScrap1.getAuction().getId());
        Assertions.assertThat(first.getBidHighestPrice()).isEqualTo(0);

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdScrap2.getAuction().getId());
        Assertions.assertThat(second.getBidHighestPrice()).isEqualTo(0);
    }

    @Test
    @DisplayName("사용자가 스크랩(찜)한 경매의 삭제되지 않은 입찰 내역이 있으면 현재 입찰가는 최고가이다.")
    public void getUserScraps_ScrapedAuctionsWithValidBids_ReturnsHighestActiveBid() {
        //given
        Category testCategory = createCategory();

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, currentUser);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, currentUser);

        Scrap createdScrap1 = createScrap(currentUser, createdAuction1);
        Scrap createdScrap2 = createScrap(currentUser, createdAuction2);

        String bidderEmail = "bidder@test.com";
        String bidderName = "bidder";

        User bidder = createUser(bidderEmail, bidderName);

        Bid auction1Bid1 = createBid(bidder, createdAuction1, 1000, false);
        Bid auction1Bid2 = createBid(bidder, createdAuction1, 1100, false);
        Bid auction2Bid1 = createBid(bidder, createdAuction2, 1000, false);
        Bid auction2Bid2 = createBid(bidder, createdAuction2, 2000, false);

        auction2Bid2.cancel();

        //when
        List<UserAuctionsResponseDto> result = userService.getUserScraps(currentUser);

        //then
        Assertions.assertThat(result).hasSize(2);

        UserAuctionsResponseDto first = result.get(0);
        UserAuctionsResponseDto second = result.get(1);

        Assertions.assertThat(first.getAuctionId()).isEqualTo(createdScrap1.getAuction().getId());
        Assertions.assertThat(first.getBidHighestPrice()).isEqualTo(auction1Bid2.getPrice());

        Assertions.assertThat(second.getAuctionId()).isEqualTo(createdScrap2.getAuction().getId());
        Assertions.assertThat(second.getBidHighestPrice()).isEqualTo(auction2Bid1.getPrice());
    }

    @Test
    @DisplayName("스크랩한 경매가 없으면 빈 리스트를 조회한다.")
    public void getUserScraps_NeverScrapedAuction_ReturnsEmptyList() {
        //given
        Category testCategory = createCategory();

        String email = "seller@test.com";
        String name = "seller";
        User seller = createUser(email, name);

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, seller);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, seller);

        //when
        List<UserAuctionsResponseDto> result = userService.getUserScraps(currentUser);

        //then
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("사용자는 결제 내역을 조회할 수 있다.")
    public void getUserPayments_ValidUser_ReturnsPaymentHistory() {
        //given
        Category testCategory = createCategory();

        String email = "seller@test.com";
        String name = "seller";
        User seller = createUser(email, name);

        String title = "test";
        Integer basePrice = 1000;
        String description = "test auction";

        Auction createdAuction1 = createAuction(title, basePrice, description, testCategory, seller);
        Auction createdAuction2 = createAuction(title, basePrice, description, testCategory, seller);

        Integer deliveryFee = 2500;
        Integer firstItemPrice = 10000;
        Integer secondItemPrice = 15000;
        Payment payment1 = createPayment(currentUser, seller, createdAuction1, firstItemPrice, deliveryFee);
        Payment payment2 = createPayment(currentUser, seller, createdAuction2, secondItemPrice, deliveryFee);

        //when
        List<UserPaymentsResponseDto> result = userService.getUserPayments(currentUser);

        //then
        Assertions.assertThat(result).hasSize(2);

        Assertions.assertThat(payment1.getAuction().getId()).isEqualTo(createdAuction1.getId());
        Assertions.assertThat(payment1.getBuyer().getId()).isEqualTo(currentUser.getId());
        Assertions.assertThat(payment1.getFinalPrice()).isEqualTo(firstItemPrice + deliveryFee);

        Assertions.assertThat(payment2.getAuction().getId()).isEqualTo(createdAuction2.getId());
        Assertions.assertThat(payment2.getFinalPrice()).isEqualTo(secondItemPrice + deliveryFee);
    }

    @Test
    @DisplayName("사용자의 결제 내역이 없으면 빈 리스트를 조회한다.")
    public void getUserPayments_NeverPayment_ReturnsEmptyList() {
        //when
        List<UserPaymentsResponseDto> result = userService.getUserPayments(currentUser);

        //then
        Assertions.assertThat(result).isEmpty();
    }

    // 헬퍼 메서드
    private User createUser(String email, String name) {
        User user = User.builder()
                .email(email)
                .name(name)
                .profileImage(email + name)
                .providerId(name)
                .provider("LOCAL")
                .build();
        return entityManager.persistAndFlush(user);
    }

    private Category createCategory() {
        Category testCategory = Category.builder()
                .name("Test Category")
                .build();
        return entityManager.persistAndFlush(testCategory);
    }

    private Auction createAuction(String title, Integer basePrice, String description, Category category, User seller) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endAt = now.plusMinutes(5);
        Auction auction = Auction.builder()
                .title(title)
                .basePrice(basePrice)
                .description(description)
                .category(category)
                .seller(seller)
                .itemCondition(ItemCondition.brand_new)
                .status(AuctionStatus.active)
                .isDeleted(false)
                .startTime(now)
                .endTime(endAt)
                .build();

        return entityManager.persistAndFlush(auction);
    }

    private Scrap createScrap(User scraper, Auction auction) {
        Scrap scrap = Scrap.builder()
                .user(scraper)
                .auction(auction)
                .build();
        return entityManager.persistAndFlush(scrap);
    }

    private Bid createBid(User bidder, Auction auction, int price, boolean isWinning) {
        Bid bid = Bid.builder()
                .bidder(bidder)
                .auction(auction)
                .price(price)
                .isWinning(isWinning)
                .isDeleted(false)
                .build();
        return entityManager.persistAndFlush(bid);
    }

    private Payment createPayment(User buyer, User seller, Auction auction, Integer itemPrice, Integer deliveryFee) {
        Payment payment = Payment.builder()
                .buyer(buyer)
                .seller(seller)
                .auction(auction)
                .recipientName("tester")
                .recipientEmail("test@test.com")
                .phoneNumber("010-1234-5678")
                .addressLine1("test1")
                .addressLine2("test2")
                .zipCode("test3")
                .itemPrice(itemPrice)
                .deliveryFee(deliveryFee)
                .finalPrice(itemPrice + 2500)
                .orderNumber("toss-test" + LocalDateTime.now())
                .status(PaymentStatus.PAID)
                .approvedAt(LocalDateTime.now())
                .build();
        return entityManager.persistAndFlush(payment);
    }
}
