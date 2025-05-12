package com.samyookgoo.palgoosam.auction.dto;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.domain.Category;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AuctionDetailResponse {
    private Long auctionId;
    private Long categoryId;
    private String title;
    private String description;
    private String sellerEmailMasked;
    private String auctionStatus;
    private String itemCondition;
    private boolean isScrap;

    private String categoryLarge;
    private String categoryMedium;
    private String categorySmall;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<ImageDto> imageUrls;

    public static AuctionDetailResponse of(Auction auction, List<AuctionImage> images) {
        List<String> categoryNames = resolveCategoryNames(auction.getCategory());

        List<ImageDto> imageDtos = images.stream()
                .map(image -> ImageDto.builder()
                        .originalName(image.getOriginalName())
                        .storeName(image.getStoreName())
                        .imageSeq(image.getImageSeq())
                        .build())
                .sorted(Comparator.comparingInt(ImageDto::getImageSeq))
                .collect(Collectors.toList());

        return AuctionDetailResponse.builder()
                .auctionId(auction.getId())
                .categoryId(auction.getCategory().getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .sellerEmailMasked(maskEmail(auction.getSeller().getEmail()))
                .auctionStatus(auction.getStatus())
                .itemCondition(auction.getItemCondition())
                .isScrap(false)     // 로그인 api 연동 시 수정해야 함
                .categoryLarge(categoryNames.size() > 0 ? categoryNames.get(0) : null)
                .categoryMedium(categoryNames.size() > 1 ? categoryNames.get(1) : null)
                .categorySmall(categoryNames.size() > 2 ? categoryNames.get(2) : null)
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .imageUrls(imageDtos)
                .build();
    }

    private static String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        String domain = email.substring(atIndex);
        String localPart = email.substring(0, atIndex);

        String masked = "*".repeat(localPart.length() - 3);

        return localPart.substring(0, 3) + masked + domain;
    }

    private static List<String> resolveCategoryNames(Category category) {
        List<String> names = new ArrayList<>();

        Category current = category;

        while (current != null) {
            names.add(current.getName());
            current = current.getParent();
        }

        Collections.reverse(names);
        return names;
    }
}
