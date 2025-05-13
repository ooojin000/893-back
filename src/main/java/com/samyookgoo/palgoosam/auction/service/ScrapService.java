package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScrapService {

    private final UserRepository userRepository;
    private final AuctionRepository auctionRepository;
    private final ScrapRepository scrapRepository;

    @Transactional
    public boolean addScrap(Long auctionId) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("경매 상품 없음"));

        if (scrapRepository.existsByUserAndAuction(user, auction)) {
            return false;
        }

        Scrap scrap = new Scrap();
        scrap.setUser(user);
        scrap.setAuction(auction);
        scrapRepository.save(scrap);
        return true;
    }

    @Transactional
    public boolean removeScrap(Long auctionId) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("사용자 없음"));

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("경매 상품 없음"));

        Optional<Scrap> optionalScrap = scrapRepository.findByUserAndAuction(user, auction);

        if (optionalScrap.isEmpty()) {
            return false;
        }
        scrapRepository.delete(optionalScrap.get());
        return true;
    }

}
