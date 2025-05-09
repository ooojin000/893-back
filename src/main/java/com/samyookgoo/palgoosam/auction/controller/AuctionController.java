package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.dto.AuctionCreateRequest;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchRequestDto;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchResponseDto;
import com.samyookgoo.palgoosam.auction.file.FileStore;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final FileStore fileStore;

    @GetMapping("/search")
    public ResponseEntity<BaseResponse<AuctionSearchResponseDto>> search(
            AuctionSearchRequestDto auctionSearchRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("정상적으로 조회되었습니다.",
                auctionService.search(auctionSearchRequestDto)));
    }

    @PostMapping
    public ResponseEntity<?> createAuction(
            @RequestPart("request") AuctionCreateRequest request,
            @RequestPart("images") List<MultipartFile> images) {

        if (images.size() < 1 || images.size() > 10) {
            return ResponseEntity.badRequest().body("이미지는 최소 1개, 최대 10개까지 업로드 가능합니다.");
        }

        List<ResultFileStore> resultFileStores = fileStore.storeFiles(images);
        Auction savedAuction = auctionService.createAuction(request, resultFileStores);

        return ResponseEntity.ok("경매 상품 등록 성공 !! ID : " + savedAuction.getId());
    }
}
