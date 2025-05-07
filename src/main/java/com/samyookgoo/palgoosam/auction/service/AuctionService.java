package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchRequestDto;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchResponseDto;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final AuctionImageRepository imageRepository;
    private final ScrapRepository scrapRepository;
    private final BidRepository bidRepository;

    public List<AuctionSearchResponseDto> search(AuctionSearchRequestDto auctionSearchRequestDto) {
        log.info("다음 조건을 검색: {}", auctionSearchRequestDto.toString());

        List<Auction> auctionList = findAuctionList(auctionSearchRequestDto);

        log.info("{}개의 경매를 찾았습니다.", auctionList.size());

        auctionList.forEach(auction -> log.debug("찾은 경매 정보: {}", auction.toString()));

        List<Long> auctionIdList = getAuctionIdList(auctionList);
        Map<Long, String> thumbnailMap = getThumbnailMap(auctionIdList);
        Map<Long, List<Bid>> bidsByAuctionMap = getBidListByAuctionMap(auctionIdList);
        Map<Long, List<Scrap>> scrapsByAuctionMap = getScrapListByAuctionMap(auctionIdList);

        List<AuctionSearchResponseDto> resultWithoutSort = auctionList.stream().map(auction -> {
                    Long auctionId = auction.getId();
                    String thumbnailUrl = thumbnailMap.get(auctionId);
                    List<Bid> bids = bidsByAuctionMap.get(auctionId);
                    List<Scrap> scraps = scrapsByAuctionMap.get(auctionId);
                    return AuctionSearchResponseDto.builder().id(auction.getId())
                            .title(auction.getTitle())
                            .startTime(auction.getStartTime())
                            .endTime(auction.getEndTime())
                            .endTime(auction.getEndTime())
                            .status(auction.getStatus())
                            .basePrice(auction.getBasePrice())
                            .thumbnailUrl(thumbnailUrl)
                            .bidderCount(bids != null ? bids.size() : 0)
                            .currentPrice(bids != null ? bids.getFirst().getPrice() : auction.getBasePrice())
                            .scrapCount(scraps.size())
//                        .isScrapped(auctionSearchResult.getIsScrapped()) <- 로그인 구현 이후 기능 추가 필요
                            .build();
                }
        ).toList();

        return this.sortAuctionSearchResponseDtoList(resultWithoutSort, auctionSearchRequestDto.getSortBy());
    }

    private List<Auction> findAuctionList(AuctionSearchRequestDto auctionSearchRequestDto) {
        return auctionRepository.findAllWithDetails(
                auctionSearchRequestDto.toAuctionSearchParam());
    }

    private List<Long> getAuctionIdList(List<Auction> auctionList) {
        return auctionList.stream().map(Auction::getId).collect(Collectors.toList());
    }

    private Map<Long, String> getThumbnailMap(List<Long> auctionIList) {
        List<AuctionImage> thumbnailList = imageRepository.findThumbnailsByAuctionIds(auctionIList);

        return thumbnailList.stream()
                .collect(Collectors.toMap(
                        image -> image.getAuction().getId(),
                        AuctionImage::getUrl,
                        (existing, replacement) -> existing
                ));
    }

    private Map<Long, List<Bid>> getBidListByAuctionMap(List<Long> auctionIdList) {
        List<Bid> bidList = bidRepository.findByAuctionIdList(auctionIdList);
        return bidList.stream()
                .collect(Collectors.groupingBy(bid -> bid.getAuction().getId()));
    }

    private Map<Long, List<Scrap>> getScrapListByAuctionMap(List<Long> auctionIdList) {

        List<Scrap> scrapList = scrapRepository.findByAuctionIdList(auctionIdList);
        return scrapList.stream()
                .collect(Collectors.groupingBy(scrap -> scrap.getAuction().getId()));
    }

    private List<AuctionSearchResponseDto> sortAuctionSearchResponseDtoList(
            List<AuctionSearchResponseDto> auctionSearchResponseDtoList, String sortBy
    ) {
        if (sortBy.equals("price_asc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionSearchResponseDto::getBasePrice)).toList();
        } else if (sortBy.equals("price_desc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionSearchResponseDto::getBasePrice).reversed()).toList();
        } else if (sortBy.equals("scrap_count_desc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionSearchResponseDto::getScrapCount).reversed()).toList();
        } else if (sortBy.equals("bidder_count_desc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionSearchResponseDto::getBidderCount).reversed()).toList();
        }

        return auctionSearchResponseDtoList;
    }
}
