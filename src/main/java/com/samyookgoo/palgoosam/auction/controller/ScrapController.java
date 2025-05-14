package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.service.ScrapService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<BaseResponse<Void>> addScrap(@PathVariable Long auctionId) {
        boolean addedScrap = scrapService.addScrap(auctionId);

        if (addedScrap) {
            return ResponseEntity.ok(BaseResponse.success("스크랩 등록 완료", null));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.error("이미 스크랩된 상품입니다.", null));
        }
    }

    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> removeScrap(@PathVariable Long auctionId) {
        boolean removedScrap = scrapService.removeScrap(auctionId);

        if (removedScrap) {
            return ResponseEntity.ok(BaseResponse.success("스크랩 취소 완료", null));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.error("스크랩된 상태가 아닙니다.", null));
        }
    }
}
