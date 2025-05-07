package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.dto.AuctionCreateRequest;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final AuctionImageRepository auctionImageRepository;

    public Auction createAuction(AuctionCreateRequest request, List<ResultFileStore> images) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("카테고리 없음"));

        // 더미 사용자
        User dummyUser = new User();
        dummyUser.setId(1L);

        LocalDateTime now = LocalDateTime.now();

        // startTime 유효성 검사
        if (request.getStartDelay() < 0 || request.getStartDelay() > 1440) {
            throw new IllegalArgumentException("경매 오픈 시간은 현재 시각 이후부터 24시간 이내여야 합니다.");
        }

        LocalDateTime startTime = now.plusMinutes(request.getStartDelay());

        // durationTime 유효성 검사
        if (request.getDurationTime() < 10 || request.getDurationTime() > 1440) {
            throw new IllegalArgumentException("경매 소요 시간은 10분 이상, 24시간(1440분) 이내여야 합니다.");
        }

        // 종료 시간 계산
        LocalDateTime endTime = startTime.plusMinutes(request.getDurationTime());

        Auction auction = Auction.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(category)
                .itemCondition(request.getItemCondition().name())
                .startTime(startTime)
                .endTime(endTime)
                .seller(dummyUser)
                .status("pending")
                .build();

        List<AuctionImage> auctionImages = new ArrayList<>();

        for (int i = 0; i < images.size(); i++) {
            ResultFileStore resultFileStore = images.get(i);
            AuctionImage img = ResultFileStore.toEntity(resultFileStore);
            img.setAuction(auction);
            img.setImageSeq(i);
            auctionImages.add(img);
        }

        auction.setAuctionImages(auctionImages);
        return auctionRepository.save(auction);
    }
}
