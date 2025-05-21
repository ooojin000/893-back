package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.dto.request.AuctionCreateRequest;
import com.samyookgoo.palgoosam.auction.dto.request.AuctionSearchRequestDto;
import com.samyookgoo.palgoosam.auction.dto.request.AuctionUpdateRequest;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionCreateResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionDetailResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionSearchResponseDto;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionUpdatePageResponse;
import com.samyookgoo.palgoosam.auction.dto.response.AuctionUpdateResponse;
import com.samyookgoo.palgoosam.auction.dto.response.RelatedAuctionResponse;
import com.samyookgoo.palgoosam.auction.file.FileStore;
import com.samyookgoo.palgoosam.auction.file.ResultFileStore;
import com.samyookgoo.palgoosam.auction.service.AuctionService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auctions")
@Tag(name = "경매", description = "경매 상품 관련 API")
public class AuctionController {

    private final AuctionService auctionService;
    private final FileStore fileStore;

    @Operation(
            summary = "경매 상품 검색",
            description = "검색 조건(키워드, 카테고리, 정렬 등)에 따라 경매 상품을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "검색 결과 조회 성공")
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<AuctionSearchResponseDto>> search(
            AuctionSearchRequestDto auctionSearchRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponse.success("정상적으로 조회되었습니다.",
                auctionService.search(auctionSearchRequestDto)));
    }

    @Operation(
            summary = "경매 상품 등록",
            description = "경매 상품 정보를 등록하고, 최소 1개에서 최대 10개의 이미지를 업로드합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "경매 등록 성공"),
            @ApiResponse(responseCode = "400", description = "이미지 개수 제한 위반 또는 유효하지 않은 요청")
    })
    @PostMapping
    public ResponseEntity<BaseResponse<AuctionCreateResponse>> createAuction(
            @RequestPart("request") @Valid AuctionCreateRequest request,
            @RequestPart("images") List<MultipartFile> images) {

        if (images == null || images.size() < 1 || images.size() > 10) {
            return ResponseEntity.badRequest()
                    .body(BaseResponse.error("이미지는 최소 1개, 최대 10개까지 업로드 가능합니다.", null));
        }

        List<ResultFileStore> storedImages = fileStore.storeFiles(images);
        AuctionCreateResponse response = auctionService.createAuction(request, storedImages);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("경매 상품 등록 성공", response));
    }

    @Operation(
            summary = "경매 상품 상세 조회",
            description = "경매 상품 ID를 기준으로 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상세 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "경매 상품을 찾을 수 없음")
    })
    @GetMapping("/{auctionId}")
    public ResponseEntity<BaseResponse<AuctionDetailResponse>> getAuction(
            @Parameter(name = "auctionId", description = "상세 정보를 조회할 경매 상품 ID", required = true)
            @PathVariable long auctionId
    ) {
        AuctionDetailResponse response = auctionService.getAuctionDetail(auctionId);
        return ResponseEntity.ok(
                BaseResponse.success("경매 상품 상세 조회 성공", response)
        );
    }

    @Operation(
            summary = "경매 상품 수정 페이지 조회",
            description = "경매 상품 수정 전, 기존 정보를 불러오기 위한 API입니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정페이지 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "경매 상품을 찾을 수 없음")
    })
    @GetMapping("/{auctionId}/update")
    public ResponseEntity<BaseResponse<AuctionUpdatePageResponse>> getAuctionUpdatePage(
            @Parameter(name = "auctionId", description = "수정할 경매 상품 ID", required = true)
            @PathVariable long auctionId
    ) {
        AuctionUpdatePageResponse response = auctionService.getAuctionUpdate(auctionId);
        return ResponseEntity.ok(BaseResponse.success("경매 상품 수정페이지 조회 성공", response));
    }

    @Operation(
            summary = "경매 상품 수정",
            description = "경매 상품 정보를 수정합니다. 이미지도 함께 수정할 수 있으며, 기존 이미지를 대체하거나 유지할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "400", description = "이미지 혹은 요청 데이터 오류"),
            @ApiResponse(responseCode = "404", description = "경매 상품을 찾을 수 없음")
    })
    @PatchMapping("/{auctionId}")
    public ResponseEntity<BaseResponse<AuctionUpdateResponse>> updateAuction(
            @Parameter(name = "auctionId", description = "수정할 경매 상품 ID", required = true)
            @PathVariable Long auctionId,
            @RequestPart(value = "request", required = false) AuctionUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) {
        AuctionUpdateResponse updated = auctionService.updateAuction(auctionId, request, images);
        return ResponseEntity.ok(
                BaseResponse.success("경매 상품 수정 성공", updated)
        );
    }

    @Operation(
            summary = "경매 상품 삭제",
            description = "경매 상품 ID를 기준으로 해당 경매를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "경매 상품을 찾을 수 없음")
    })
    @DeleteMapping("/{auctionId}")
    public ResponseEntity<BaseResponse<Void>> deleteAuction(
            @Parameter(name = "auctionId", description = "삭제할 경매 상품 ID", required = true)
            @PathVariable long auctionId
    ) {
        auctionService.deleteAuction(auctionId);
        return ResponseEntity.ok(BaseResponse.success("경매 상품 삭제 성공", null));
    }

    @Operation(
            summary = "연관 경매 상품 조회",
            description = "현재 경매 상품과 같은 소분류 또는 중분류 카테고리에 속한 경매 중 상품 10개 이내를 추천합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "연관 상품 조회 성공"),
            @ApiResponse(responseCode = "404", description = "기준 경매 상품을 찾을 수 없음")
    })
    @GetMapping("/{auctionId}/related")
    public ResponseEntity<BaseResponse<List<RelatedAuctionResponse>>> getRelatedAuctions(
            @Parameter(name = "auctionId", description = "연관 상품을 조회할 기준 경매 ID", required = true)
            @PathVariable Long auctionId
    ) {
        List<RelatedAuctionResponse> related = auctionService.getRelatedAuctions(auctionId);
        return ResponseEntity.ok(BaseResponse.success("연관 경매 상품 조회 성공", related));
    }

    @GetMapping
    public ResponseEntity<BaseResponse<AuctionSearchResponseDto>> getAuctions() {

        return ResponseEntity.ok(BaseResponse.success("정상적으로 조회되었습니다.", auctionService.getAuctions()));
    }
}
