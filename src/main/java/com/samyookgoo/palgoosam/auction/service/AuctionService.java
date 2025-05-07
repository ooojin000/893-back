package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final AuctionImageRepository auctionImageRepository;

    public void uploadAuctionImages(List<ResultFileStore> images, int mainImageIndex) {
        Auction auction = auctionRepository.findById(5L)
                .orElseThrow(() -> new NoSuchElementException("해당 경매가 존재하지 않습니다."));

        List<AuctionImage> imageList = images.stream()
                .map(ResultFileStore::toEntity)
                .collect(Collectors.toList());

        for (int i = 0; i < images.size(); i++) {
            AuctionImage img = imageList.get(i);
            img.setAuction(auction);
            img.setImageSeq(i);
            img.setIsMain(mainImageIndex == i);
        }

        auctionImageRepository.saveAll(imageList);
    }
}