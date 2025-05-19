package com.samyookgoo.palgoosam.user.controller;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.service.AuctionImageService;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.service.BidService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.dto.UserAuctionsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserBidsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserInfoResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserPaymentsResponseDto;
import com.samyookgoo.palgoosam.user.service.UserService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final AuctionImageService auctionImageService;
    private final BidService bidService;
    private final AuctionService auctionService;

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/user-info")
    public ResponseEntity<BaseResponse<UserInfoResponseDto>> getUserInfo() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        UserInfoResponseDto dto = new UserInfoResponseDto(
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getProvider()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("사용자 정보가 성공적으로 조회됐습니다.", dto));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/bids")
    public ResponseEntity<BaseResponse<List<UserBidsResponseDto>>> getUserBids() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<Bid> bids = userService.getUserBidsByUserId(user.getId());
        List<Long> auctionIds = auctionService.getAuctionIdsByBids(bids);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        // 경매별 최고 입찰가
        Map<Long, Integer> maxBidMap = bidService.getAuctionMaxPrices(auctionIds);

        List<UserBidsResponseDto> dtos = userService.getUserBidsResponse(bids, imageMap, maxBidMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("내 입찰이 성공적으로 조회됐습니다.", dtos));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/auctions")
    public ResponseEntity<BaseResponse<List<UserAuctionsResponseDto>>> getUserAuctions() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<Auction> auctions = userService.getUserAuctionsByUserId(user.getId());
        List<Long> auctionIds = auctionService.getAuctionIdsByAuctions(auctions);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        // 경매별 최고 입찰가
        Map<Long, Integer> maxBidMap = bidService.getAuctionMaxPrices(auctionIds);

        List<UserAuctionsResponseDto> dtos = userService.getUserAuctionsResponse(auctions, imageMap, maxBidMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("내 경매가 성공적으로 조회됐습니다.", dtos));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/scraps")
    public ResponseEntity<BaseResponse<List<UserAuctionsResponseDto>>> getUserScraps() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<Scrap> scraps = userService.getUserScrapsByUserId(user.getId());
        List<Long> auctionIds = auctionService.getAuctionIdsByScraps(scraps);
        List<Auction> auctions = auctionService.getAuctionsByAuctionIds(auctionIds);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        // 경매별 최고 입찰가
        Map<Long, Integer> maxBidMap = bidService.getAuctionMaxPrices(auctionIds);

        List<UserAuctionsResponseDto> dtoList = userService.getUserAuctionsResponse(auctions, imageMap, maxBidMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("스크랩 경매가 성공적으로 조회됐습니다.", dtoList));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/payments")
    public ResponseEntity<BaseResponse<List<UserPaymentsResponseDto>>> getUserPayments() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<Payment> payments = userService.getUserPaymentsByUserId(user.getId());

        List<Long> auctionIds = auctionService.getAuctionIdsByPayment(payments);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        List<UserPaymentsResponseDto> dtoList = userService.getUserPaymentsResponse(payments, imageMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("내 결제 내역이 성공적으로 조회됐습니다.", dtoList));
    }
}