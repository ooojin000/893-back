package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.constant.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuctionStatusService {
    private final AuctionRepository auctionRepository;

    @Transactional
    public void updateStatusToActive(Long auctionId) {
        auctionRepository.findById(auctionId).ifPresentOrElse(auction -> {
            auction.setStatus(AuctionStatus.active);
        }, () -> {
            log.warn("존재하지 않는 auctionId: {}", auctionId);
        });
    }

    @Transactional
    public void updateStatusToCompleted(Long auctionId) {
        auctionRepository.findById(auctionId).ifPresentOrElse(auction -> {
            auction.setStatus(AuctionStatus.completed);
        }, () -> {
            log.warn("존재하지 않는 auctionId: {}", auctionId);
        });
    }

    @Transactional
    public void updateStatusToActive(Auction auction) {
        auction.setStatus(AuctionStatus.active);
    }

    @Transactional
    public void updateStatusToCompleted(Auction auction) {
        auction.setStatus(AuctionStatus.completed);
    }
}
