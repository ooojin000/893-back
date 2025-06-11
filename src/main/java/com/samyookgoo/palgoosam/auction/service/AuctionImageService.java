package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionImageService {
    private final AuctionImageRepository auctionImageRepository;

    public Map<Long, String> getAuctionMainImages(List<Long> auctionIds) {
        return auctionImageRepository
                .findMainImagesByAuctionIds(auctionIds)
                .stream()
                .collect(Collectors.toMap(
                        auctionImage -> auctionImage.getAuction().getId(),
                        AuctionImage::getUrl));
    }
}
