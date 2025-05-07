package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.dto.AuctionCreateRequest;
import com.samyookgoo.palgoosam.auction.file.FileStore;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createAuctionWithImages(
            @RequestPart("request") AuctionCreateRequest request,
            @RequestPart("images") List<MultipartFile> images) {

        if (images.size() < 1 || images.size() > 10) {
            return ResponseEntity.badRequest().body("이미지는 최소 1개, 최대 10개까지 업로드 가능합니다.");
        }

        List<ResultFileStore> resultFileStores = new ArrayList<>();

        if (!images.isEmpty() && !images.get(0).isEmpty()) {
            resultFileStores = fileStore.storeFiles(images);
            auctionService.uploadAuctionImages(resultFileStores, request.getMainImageIndex());
        }

        return ResponseEntity.ok("이미지 등록 성공");
    }
}
