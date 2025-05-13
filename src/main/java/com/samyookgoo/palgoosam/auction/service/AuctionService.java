package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.dto.AuctionCreateRequest;
import com.samyookgoo.palgoosam.auction.dto.AuctionDetailResponse;
import com.samyookgoo.palgoosam.auction.dto.AuctionUpdateRequest;
import com.samyookgoo.palgoosam.auction.dto.AuctionListItemDto;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchRequestDto;
import com.samyookgoo.palgoosam.auction.dto.AuctionSearchResponseDto;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.payment.domain.Payment;
import com.samyookgoo.palgoosam.user.domain.Scrap;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AuctionService {
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final AuctionImageRepository auctionImageRepository;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final BidRepository bidRepository;

    public List<Long> getAuctionIdsByAuctions(List<Auction> auctions) {
        return auctions.stream()
                .map(Auction::getId)
                .distinct()
                .toList();
    }

    public List<Long> getAuctionIdsByBids(List<Bid> bids) {
        return bids.stream()
                .map(b -> b.getAuction().getId())
                .distinct()
                .toList();
    }

    public List<Long> getAuctionIdsByScarps(List<Scrap> scraps) {
        return scraps.stream()
                .map(s -> s.getAuction().getId())
                .distinct()
                .toList();
    }

    public List<Long> getAuctionIdsByPayment(List<Payment> payments) {
        return payments.stream()
                .map(p -> p.getAuction().getId())
                .distinct()
                .toList();
    }

    public Auction createAuction(AuctionCreateRequest request, List<ResultFileStore> images) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new NoSuchElementException("카테고리 없음"));

        User user = userRepository.findById(1L)
                .orElseThrow(() -> new NoSuchElementException("사용자 없음"));

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
                .seller(user)
                .status("pending")
                .build();

        List<AuctionImage> auctionImages = toAuctionImages(images, auction);

        auction.setAuctionImages(auctionImages);
        return auctionRepository.save(auction);
    }

    public AuctionDetailResponse getAuctionDetail(Long auctionId) {
        Auction auction = auctionRepository.findByIdWithCategoryAndSeller(auctionId)
                .orElseThrow(() -> new NoSuchElementException("경매 상품이 존재하지 않습니다."));

        List<AuctionImage> images = auctionImageRepository.findByAuctionId(auctionId);

        return AuctionDetailResponse.of(auction, images);
    }

    @Transactional
    public AuctionDetailResponse updateAuction(Long auctionId, AuctionUpdateRequest dto) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("경매 상품을 찾을 수 없습니다."));

        // 경매 시작 30분 전 수정 제한
        if (auction.getStartTime().minusMinutes(30).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("경매 시작 30분전부터는 수정이 불가합니다.");
        }

        if (dto.getTitle() != null) {
            auction.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            auction.setDescription(dto.getDescription());
        }
        if (dto.getBasePrice() != null) {
            if (dto.getBasePrice() < 0) {
                throw new IllegalArgumentException("경매 시작가는 0원 이상이어야 합니다.");
            }
            auction.setBasePrice(dto.getBasePrice());
        }
        if (dto.getItemCondition() != null) {
            auction.setItemCondition(dto.getItemCondition().name());
        }
        if (dto.getStartDelay() != null) {
            auction.setStartTime(LocalDateTime.now().plusMinutes(dto.getStartDelay()));
        }
        if (dto.getDurationTime() != null && auction.getStartTime() != null) {
            auction.setEndTime(auction.getStartTime().plusMinutes(dto.getDurationTime()));
        }
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리 없음"));
            auction.setCategory(category);
        }

        if (dto.getImageUrls() != null) {
            auctionImageRepository.deleteByAuctionId(auctionId);

            for (int i = 0; i < dto.getImageUrls().size(); i++) {
                AuctionImage image = AuctionImage.builder()
                        .auction(auction)
                        .originalName(dto.getImageUrls().get(i))
                        .storeName(dto.getImageUrls().get(i))
                        .imageSeq(i)
                        .build();
                auction.getAuctionImages().add(image);
            }
        }

        List<AuctionImage> images = auctionImageRepository.findByAuctionId(auction.getId());
        return AuctionDetailResponse.of(auction, images);
    }

    public List<AuctionImage> toAuctionImages(List<ResultFileStore> result, Auction auction) {
        List<AuctionImage> auctionImages = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            ResultFileStore resultFileStore = result.get(i);
            AuctionImage img = ResultFileStore.toEntity(resultFileStore);
            img.setAuction(auction);
            img.setImageSeq(i);
            auctionImages.add(img);
        }

        return auctionImages;
    }


    public AuctionSearchResponseDto search(AuctionSearchRequestDto auctionSearchRequestDto) {
        log.info("다음 조건을 검색: {}", auctionSearchRequestDto.toString());
        List<Auction> auctionList = findAuctionList(auctionSearchRequestDto);
        Long auctionCount = Long.valueOf(auctionList.size());

        log.info("{}개의 경매를 찾았습니다.", auctionList.size());

        if (auctionList.isEmpty()) {
            return new AuctionSearchResponseDto(auctionCount, new ArrayList<>());
        }

        auctionList.forEach(auction -> log.debug("찾은 경매 정보: {}", auction.toString()));

        List<Long> auctionIdList = getAuctionIdList(auctionList);
        Map<Long, String> thumbnailMap = getThumbnailMap(auctionIdList);
        Map<Long, List<Bid>> bidsByAuctionMap = getBidListByAuctionMap(auctionIdList);
        Map<Long, List<Scrap>> scrapsByAuctionMap = getScrapListByAuctionMap(auctionIdList);

        List<AuctionListItemDto> resultWithoutSort = auctionList.stream().map(auction -> {
                    Long auctionId = auction.getId();
                    String thumbnailUrl = thumbnailMap.get(auctionId);
                    List<Bid> bids = bidsByAuctionMap.get(auctionId);
                    List<Scrap> scraps = scrapsByAuctionMap.get(auctionId);
                    return AuctionListItemDto.builder().id(auction.getId())
                            .title(auction.getTitle())
                            .startTime(auction.getStartTime())
                            .endTime(auction.getEndTime())
                            .status(auction.getStatus())
                            .basePrice(auction.getBasePrice())
                            .thumbnailUrl(thumbnailUrl)
                            .bidderCount(bids != null ? bids.size() : 0)
                            .currentPrice(bids != null ? bids.getFirst().getPrice() : auction.getBasePrice())
                            .scrapCount(scraps != null ? scraps.size() : 0)
//                        .isScrapped(auctionSearchResult.getIsScrapped()) <- 로그인 구현 이후 기능 추가 필요
                            .build();
                }
        ).toList();

        return new AuctionSearchResponseDto(auctionCount,
                this.sortAuctionListItemDtoList(resultWithoutSort, auctionSearchRequestDto.getSortBy()));
    }

    private List<Auction> findAuctionList(AuctionSearchRequestDto auctionSearchRequestDto) {
        return auctionRepository.findAllWithDetails(
                auctionSearchRequestDto.toAuctionSearchParam());
    }

    private List<Long> getAuctionIdList(List<Auction> auctionList) {
        return auctionList.stream().map(Auction::getId).collect(Collectors.toList());
    }

    private Map<Long, String> getThumbnailMap(List<Long> auctionIList) {
        List<AuctionImage> thumbnailList = auctionImageRepository.findThumbnailsByAuctionIds(auctionIList);

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

    private List<AuctionListItemDto> sortAuctionListItemDtoList(
            List<AuctionListItemDto> auctionSearchResponseDtoList, String sortBy
    ) {
        if (sortBy.equals("price_asc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionListItemDto::getBasePrice)).toList();
        } else if (sortBy.equals("price_desc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionListItemDto::getBasePrice).reversed()).toList();
        } else if (sortBy.equals("scrap_count_desc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionListItemDto::getScrapCount).reversed()).toList();
        } else if (sortBy.equals("bidder_count_desc")) {
            return auctionSearchResponseDtoList.stream()
                    .sorted(Comparator.comparing(AuctionListItemDto::getBidderCount).reversed()).toList();
        }

        return auctionSearchResponseDtoList;
    }
}
