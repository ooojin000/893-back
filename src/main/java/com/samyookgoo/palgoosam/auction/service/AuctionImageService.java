package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.bid.projection.MainImageProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuctionImageService {
    private final AuctionImageRepository auctionImageRepository;

    public Map<Long, String> getAuctionMainImages(List<Long> auctionIds) {
        return auctionImageRepository
                .findMainImagesByAuctionIds(auctionIds)
                .stream()
                .collect(Collectors.toMap(
                        MainImageProjection::getAuctionId,
                        MainImageProjection::getImageUrl
                ));
    }
}
