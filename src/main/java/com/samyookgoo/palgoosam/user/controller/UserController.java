package com.samyookgoo.palgoosam.user.controller;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.service.AuctionImageService;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.bid.service.BidService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.payment.repository.PaymentRepository;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.dto.UserAuctionsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserBidsResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserInfoResponseDto;
import com.samyookgoo.palgoosam.user.dto.UserPaymentsResponseDto;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.samyookgoo.palgoosam.auth.service.AuthService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final AuctionImageService auctionImageService;
    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final ScrapRepository scrapRepository;
    private final PaymentRepository paymentRepository;
    private final BidService bidService;
    private final AuctionService auctionService;

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/user-info")
    public ResponseEntity<BaseResponse> getUserInfo() {
        User user = authService.getCurrentUser();

        UserInfoResponseDto dto = new UserInfoResponseDto(
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getProvider()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("사용자 정보가 성공적으로 조회됐습니다.",dto));
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/bids")
    public ResponseEntity<BaseResponse> getUserBids() {
        User user = authService.getCurrentUser();

        List<Bid> bids = bidRepository.findAllByBidder_Id(user.getId());
        List<Long> auctionIds = auctionService.getAuctionIdsByBids(bids);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        // 경매별 최고 입찰가
        Map<Long, Integer> maxBidMap = bidService.getAuctionMaxPrices(auctionIds);

        List<UserBidsResponseDto> dtos = userService.getUserBidsResponse(bids,imageMap,maxBidMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("내 입찰이 성공적으로 조회됐습니다.",dtos));
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/auctions")
    public ResponseEntity<BaseResponse> getUserAuctions() {
        User user = authService.getCurrentUser();

        List<Auction> auctions = auctionRepository.findAllBySeller_Id(user.getId());
        List<Long> auctionIds = auctionService.getAuctionIdsByAuctions(auctions);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        // 경매별 최고 입찰가
        Map<Long, Integer> maxBidMap = bidService.getAuctionMaxPrices(auctionIds);

        List<UserAuctionsResponseDto> dtos = userService.getUserAuctionsResponse(auctions,imageMap,maxBidMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("내 경매가 성공적으로 조회됐습니다.",dtos));
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/scraps")
    public ResponseEntity<BaseResponse> getUserScraps() {
        User user = authService.getCurrentUser();

        List<Scrap> scraps = scrapRepository.findAllByUser_Id(user.getId());
        List<Long> auctionIds = auctionService.getAuctionIdsByScarps(scraps);
        List<Auction> auctions = auctionRepository.findAllById(auctionIds);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        // 경매별 최고 입찰가
        Map<Long, Integer> maxBidMap = bidService.getAuctionMaxPrices(auctionIds);

        List<UserAuctionsResponseDto> dtos = userService.getUserAuctionsResponse(auctions,imageMap,maxBidMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("스크랩 경매가 성공적으로 조회됐습니다.",dtos));
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/payments")
    public ResponseEntity<BaseResponse> getUserPayments() {
        User user = authService.getCurrentUser();

        List<Payment> payments = paymentRepository.findAllByBuyer_Id(user.getId());

        List<Long> auctionIds = auctionService.getAuctionIdsByPayment(payments);

        // 메인 이미지 URL
        Map<Long, String> imageMap = auctionImageService.getAuctionMainImages(auctionIds);

        List<UserPaymentsResponseDto> dtos = userService.getUserPaymentsResponse(payments,imageMap);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("내 결제 내역이 성공적으로 조회됐습니다.",dtos));
    }
}