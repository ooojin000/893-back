package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.dto.AuctionSearchRequestDto;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchResponseDto;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @GetMapping("/search")
    public ResponseEntity<List<AuctionSearchResponseDto>> search(AuctionSearchRequestDto auctionSearchRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.search(auctionSearchRequestDto));
    }
}
