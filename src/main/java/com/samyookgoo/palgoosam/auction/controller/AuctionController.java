package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.dto.AuctionSearchRequestDto;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchResponseDto;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @GetMapping("/api/auctions/search")
    public ResponseEntity<List<AuctionSearchResponseDto>> search(AuctionSearchRequestDto auctionSearchRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(auctionService.search(auctionSearchRequestDto));
    }
}
