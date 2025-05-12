package com.samyookgoo.palgoosam.auction.service;

import com.samyookgoo.palgoosam.auction.domain.Auction;
import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import com.samyookgoo.palgoosam.auction.domain.AuctionStatus;
import com.samyookgoo.palgoosam.auction.domain.Category;
import com.samyookgoo.palgoosam.auction.dto.request.AuctionCreateRequest;
import com.samyookgoo.palgoosam.auction.dto.request.AuctionImageRequest;
import com.samyookgoo.palgoosam.auction.dto.request.AuctionUpdateRequest;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionCreateResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionDetailResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionImageResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionUpdatePageResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionUpdateResponse;
import com.samyookgoo.palgoosam.auction.dto.response.CategoryResponse;
import com.samyookgoo.palgoosam.auction.file.FileStore;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.repository.AuctionImageRepository;
import com.samyookgoo.palgoosam.auction.repository.AuctionRepository;
import com.samyookgoo.palgoosam.auction.repository.CategoryRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.ScrapRepository;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class AuctionService {

    private final AuctionImageRepository auctionImageRepository;
    private final AuctionRepository auctionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ScrapRepository scrapRepository;
    private final FileStore fileStore;

    // 경매 상품 등록
    @Transactional
    public AuctionCreateResponse createAuction(AuctionCreateRequest request, List<ResultFileStore> resultFileStores) {
        Category category = categoryRepository.findById(request.getCategory().getId())
                .orElseThrow(() -> new NoSuchElementException("카테고리가 존재하지 않습니다."));

        validateLeafCategory(category);

        User user = userRepository.findById(1L).orElseThrow(() -> new NoSuchElementException("사용자가 존재하지 않습니다."));

        validateAuctionTime(request.getStartDelay(), request.getDurationTime());

        validateBasePrice(request.getBasePrice());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusMinutes(request.getStartDelay());
        LocalDateTime endTime = startTime.plusMinutes(request.getDurationTime());

        Auction auction = Auction.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .basePrice(request.getBasePrice())
                .itemCondition(request.getItemCondition())
                .startTime(startTime)
                .endTime(endTime)
                .category(category)
                .seller(user)
                .status(AuctionStatus.pending)
                .build();

        auctionRepository.save(auction);

        List<AuctionImageResponse> imageResponses = new ArrayList<>();
        for (int i = 0; i < resultFileStores.size(); i++) {
            ResultFileStore file = resultFileStores.get(i);
            AuctionImage image = ResultFileStore.toEntity(file, auction, i);
            auctionImageRepository.save(image);

            imageResponses.add(
                    AuctionImageResponse.builder()
                            .originalName(image.getOriginalName())
                            .storeName(image.getStoreName())
                            .url(image.getUrl())
                            .imageSeq(image.getImageSeq())
                            .build());
        }

        return AuctionCreateResponse.builder()
                .title(auction.getTitle())
                .description(auction.getDescription())
                .basePrice(auction.getBasePrice())
                .itemCondition(auction.getItemCondition())
                .startDelay(request.getStartDelay())
                .durationTime(request.getDurationTime())
                .category(CategoryResponse.builder()
                        .id(category.getId())
                        .mainCategory(request.getCategory().getMainCategory())
                        .subCategory(request.getCategory().getSubCategory())
                        .detailCategory(request.getCategory().getDetailCategory()).build())
                .images(imageResponses)
                .build();
    }

    // 경매 상품 상세 조회
    @Transactional(readOnly = true)
    public AuctionDetailResponse getAuctionDetail(Long auctionId) {
        Long loginUserId = getLoginUserId();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("경매 상품이 존재하지 않습니다."));

        boolean isSeller = auction.getSeller().getId().equals(loginUserId);

        List<AuctionImage> images = auctionImageRepository.findByAuctionId(auctionId);

        List<AuctionImageResponse> imageResponses = images.stream()
                .map(AuctionImageResponse::from)
                .collect(Collectors.toList());

        AuctionImageResponse mainImage = imageResponses.stream()
                .filter(img -> img.getImageSeq() == 0).findFirst()
                .orElse(null);

        CategoryResponse categoryResponse = CategoryResponse.from(auction.getCategory());

        String email = auction.getSeller().getEmail();
        String maskedEmail = maskEmail(email);

        boolean isScrap = scrapRepository.existsByUserIdAndAuctionId(loginUserId, auctionId);
        int scrapCount = scrapRepository.countByAuctionId(auctionId);

        return AuctionDetailResponse.builder()
                .auctionId(auction.getId())
                .categoryId(auction.getCategory().getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .sellerEmailMasked(maskedEmail)
                .status(auction.getStatus())
                .itemCondition(auction.getItemCondition())
                .isScrap(isScrap)
                .scrapCount(scrapCount)
                .isSeller(isSeller)
                .category(categoryResponse)
                .startTime(auction.getStartTime())
                .endTime(auction.getEndTime())
                .mainImage(mainImage)
                .images(imageResponses)
                .build();
    }

    // 경매 상품 수정페이지 조회
    @Transactional(readOnly = true)
    public AuctionUpdatePageResponse getAuctionUpdate(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("경매 상품이 존재하지 않습니다."));

        List<AuctionImage> images = auctionImageRepository.findByAuctionId(auctionId);

        List<AuctionImageResponse> imageResponses = images.stream()
                .map(AuctionImageResponse::from)
                .collect(Collectors.toList());

        AuctionImageResponse mainImage = imageResponses.stream()
                .filter(img -> img.getImageSeq() == 0).findFirst()
                .orElse(null);

        CategoryResponse categoryResponse = CategoryResponse.from(auction.getCategory());

        return AuctionUpdatePageResponse.builder()
                .auctionId(auction.getId())
                .categoryId(auction.getCategory().getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .status(auction.getStatus())
                .itemCondition(auction.getItemCondition())
                .category(categoryResponse)
                .mainImage(mainImage)
                .images(imageResponses)
                .build();
    }

    // 경매 상품 수정
    @Transactional
    public AuctionUpdateResponse updateAuction(Long auctionId, AuctionUpdateRequest request,
                                               List<MultipartFile> images) {
        // 1. 경매 상품 조회
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new NoSuchElementException("경매 상품이 존재하지 않습니다."));

        // 경매 시작 10분 전이면 수정 불가
        if (auction.getStartTime().minusMinutes(10).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("경매 시작 10분 전부터는 수정이 불가능합니다.");
        }

        // 2. 필드 수정
        auction.setTitle(request.getTitle());
        auction.setDescription(request.getDescription());
        auction.setBasePrice(request.getBasePrice());
        auction.setItemCondition(request.getItemCondition());

        Category category = categoryRepository.findById(request.getCategory().getId())
                .orElseThrow(() -> new NoSuchElementException("카테고리가 존재하지 않습니다."));

        validateLeafCategory(category);
        auction.setCategory(category);

        validateAuctionTime(request.getStartDelay(), request.getDurationTime());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.plusMinutes(request.getStartDelay());
        LocalDateTime endTime = startTime.plusMinutes(request.getDurationTime());

        auction.setStartTime(startTime);
        auction.setEndTime(endTime);

        validateBasePrice(request.getBasePrice());

        // 3. 기존 이미지 조회
        List<AuctionImage> existingImages = auctionImageRepository.findByAuctionId(auctionId);

        List<Long> requestImageIds = request.getImages().stream()
                .map(AuctionImageRequest::getImageId)
                .filter(Objects::nonNull).collect(Collectors.toList());

        List<AuctionImage> imagesToDelete = existingImages.stream()
                .filter(img -> !requestImageIds.contains(img.getId())).collect(Collectors.toList());

        for (AuctionImage image : imagesToDelete) {
            auctionImageRepository.delete(image);
            fileStore.delete(image.getStoreName());
        }

        // 4. 새 이미지 저장
        List<ResultFileStore> storedFiles = fileStore.storeFiles(images != null ? images : List.of());
        List<AuctionImage> finalImages = new ArrayList<>();

        int newFileIndex = 0;

        try {
            for (AuctionImageRequest imageRequest : request.getImages()) {
                if (imageRequest.getImageId() != null) {
                    // 기존 이미지 시퀀스 수정
                    AuctionImage image = existingImages.stream()
                            .filter(i -> i.getId().equals(imageRequest.getImageId())).findFirst()
                            .orElseThrow(() -> new NoSuchElementException("기존 이미지가 존재하지 않습니다."));

                    image.setImageSeq(imageRequest.getImageSeq());
                    image.setAuction(auction);
                    auctionImageRepository.save(image);
                    finalImages.add(image);

                } else {
                    // 새 이미지 요청이 있는 경우에만 저장 시도
                    if (storedFiles.isEmpty()) {
                        throw new IllegalStateException("새 이미지 요청이 있으나 업로드된 파일이 없습니다.");
                    }

                    if (newFileIndex >= storedFiles.size()) {
                        throw new IllegalStateException("업로드된 새 이미지 수가 부족합니다.");
                    }

                    ResultFileStore file = storedFiles.get(newFileIndex++);
                    AuctionImage image = ResultFileStore.toEntity(file, auction, imageRequest.getImageSeq());
                    auctionImageRepository.save(image);
                    finalImages.add(image);
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 업로드된 새 이미지 파일 삭제
            for (ResultFileStore file : storedFiles) {
                fileStore.delete(file.getStoreFileName());
            }
            throw e;
        }

        // 5. 응답 DTO 구성
        List<AuctionImageResponse> imageResponses = finalImages.stream()
                .sorted(Comparator.comparingInt(AuctionImage::getImageSeq))
                .map(AuctionImageResponse::from).toList();

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(category.getId())
                .mainCategory(request.getCategory().getMainCategory())
                .subCategory(request.getCategory().getSubCategory())
                .detailCategory(request.getCategory().getDetailCategory())
                .build();

        return AuctionUpdateResponse.builder()
                .auctionId(auction.getId())
                .title(auction.getTitle())
                .description(auction.getDescription())
                .basePrice(auction.getBasePrice())
                .itemCondition(auction.getItemCondition())
                .startDelay(request.getStartDelay())
                .durationTime(request.getDurationTime())
                .category(categoryResponse)
                .images(imageResponses)
                .build();
    }

    @Transactional
    public void deleteAuction(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new EntityNotFoundException("경매 상품 찾을 수 없음."));

        List<AuctionImage> images = auctionImageRepository.findByAuctionId(auctionId);

        for (AuctionImage image : images) {
            fileStore.delete(image.getStoreName());
        }

        auctionImageRepository.deleteAll(images);
        auctionRepository.delete(auction);
    }

    private static String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        String domain = email.substring(atIndex);
        String localPart = email.substring(0, atIndex);

        String masked = "*".repeat(localPart.length() - 3);

        return localPart.substring(0, 3) + masked + domain;
    }

    private void validateLeafCategory(Category category) {
        if (!category.getChildren().isEmpty()) {
            throw new IllegalArgumentException("소분류 카테고리만 선택할 수 있습니다.");
        }
    }

    private void validateBasePrice(int basePrice) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("시작가는 0원 이상이어야 합니다.");
        }
    }

    private void validateAuctionTime(int startDelay, int durationTime) {
        if (startDelay <= 0 || startDelay >= 1440) {
            throw new IllegalArgumentException("경매 오픈 시간은 현재 시각 이후부터 24시간 이내여야 합니다.");
        }
        if (durationTime <= 10 || durationTime >= 1440) {
            throw new IllegalArgumentException("경매 소요 시간은 10분 이상, 24시간(1440분) 이내여야 합니다.");
        }
    }

    private Long getLoginUserId() {
        return 1L; // 임시 더미 로그인 사용자 ID
    }

}
