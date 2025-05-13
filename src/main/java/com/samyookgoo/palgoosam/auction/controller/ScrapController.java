package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.service.ScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions/{auctionId}/scrap")
public class ScrapController {

    private final ScrapService scrapService;

    @PostMapping
    public ResponseEntity<String> addScrap(@PathVariable Long auctionId) {
        boolean addedScrap = scrapService.addScrap(auctionId);
        return ResponseEntity.ok(addedScrap ? "스크랩 등록 완료" : "이미 스크랩된 상품입니다.");
    }

    @DeleteMapping
    public ResponseEntity<String> removeScrap(@PathVariable Long auctionId) {
        boolean removedScrap = scrapService.removeScrap(auctionId);
        return ResponseEntity.ok(removedScrap ? "스크랩 취소 완료" : "스크랩된 상태가 아닙니다.");
    }
}
