package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.dto.request.AuctionCreateRequest;
import com.samyookgoo.palgoosam.auction.dto.request.AuctionUpdateRequest;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionCreateResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionDetailResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionUpdatePageResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionUpdateResponse;
import com.samyookgoo.palgoosam.auction.file.FileStore;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping
    public ResponseEntity<AuctionCreateResponse> createAuction(
            @RequestPart("request") @Valid AuctionCreateRequest request,
            @RequestPart("images") List<MultipartFile> images) {

        if (images == null || images.size() < 1 || images.size() > 10) {
            throw new IllegalArgumentException("이미지는 최소 1개, 최대 10개까지 업로드 가능합니다.");
        }

        List<ResultFileStore> storedImages = fileStore.storeFiles(images);
        AuctionCreateResponse response = auctionService.createAuction(request, storedImages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{auctionId}")
    public ResponseEntity<AuctionDetailResponse> getAuction(@PathVariable long auctionId) {
        AuctionDetailResponse response = auctionService.getAuctionDetail(auctionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{auctionId}/update")
    public ResponseEntity<AuctionUpdatePageResponse> getAuctionUpdatePage(@PathVariable long auctionId) {
        AuctionUpdatePageResponse response = auctionService.getAuctionUpdate(auctionId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{auctionId}")
    public ResponseEntity<AuctionUpdateResponse> updateAuction(
            @PathVariable Long auctionId,
            @RequestPart("request") @Valid AuctionUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        AuctionUpdateResponse updated = auctionService.updateAuction(auctionId, request, images);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{auctionId}")
    public ResponseEntity<?> deleteAuction(@PathVariable long auctionId) {
        auctionService.deleteAuction(auctionId);
        return ResponseEntity.ok("삭제 완료");
    }
}
