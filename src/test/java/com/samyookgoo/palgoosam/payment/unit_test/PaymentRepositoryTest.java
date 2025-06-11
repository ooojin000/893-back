package com.samyookgoo.palgoosam.payment.unit_test;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.constant.ItemCondition;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.payment.constant.PaymentStatus;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.domain.PaymentForMyPageProjection;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("PaymentRepository 유닛 테스트")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private UserRepository userRepository;

    private User currentUser;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = createCategory();
        currentUser = createUser("currentUser@test.com", "currentUser");
    }

    @AfterEach
    void tearDown() {
        paymentRepository.deleteAllInBatch();
        auctionRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자의 결제 내역을 모두 조회한다.")
    public void findAllPaymentForMyPageProjectionByBuyerId_ValidBuyer_ReturnsAllPayments() {
        //given

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
        List<PaymentForMyPageProjection> result = paymentRepository.findAllPaymentForMyPageProjectionByBuyerId(
                currentUser.getId());

        //then
        Assertions.assertThat(result).hasSize(2);

        Assertions.assertThat(payment1.getAuction().getId()).isEqualTo(createdAuction1.getId());
        Assertions.assertThat(payment1.getBuyer().getId()).isEqualTo(currentUser.getId());
        Assertions.assertThat(payment1.getFinalPrice()).isEqualTo(firstItemPrice + deliveryFee);

        Assertions.assertThat(payment2.getAuction().getId()).isEqualTo(createdAuction2.getId());
        Assertions.assertThat(payment2.getFinalPrice()).isEqualTo(secondItemPrice + deliveryFee);
    }

    @Test
    @DisplayName("사용자의 결제 내역이 없다면 빈 리스트를 조회한다.")
    public void findAllPaymentForMyPageProjectionByBuyerId_NeverPayment_ReturnsEmptyList() {

        //when
        List<PaymentForMyPageProjection> result = paymentRepository.findAllPaymentForMyPageProjectionByBuyerId(
                currentUser.getId());

        //then
        Assertions.assertThat(result).isEmpty();
    }

    // 헬퍼 메서드
    private Category createCategory() {
        Category testCategory = Category.builder()
                .name("Test Category")
                .build();
        return categoryRepository.save(testCategory);
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

        return auctionRepository.save(auction);
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
        return paymentRepository.save(payment);
    }
}
