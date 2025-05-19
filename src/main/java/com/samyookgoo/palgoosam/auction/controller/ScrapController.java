package com.samyookgoo.palgoosam.auction.controller;

import com.samyookgoo.palgoosam.auction.service.ScrapService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auctions/{auctionId}/scrap")
@Tag(name = "스크랩", description = "스크랩 관련 API")
public class ScrapController {

    private final ScrapService scrapService;

    @Operation(
            summary = "스크랩 등록",
            description = "특정 경매 상품을 스크랩합니다. 이미 스크랩된 상품일 경우 409(CONFLICT)를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스크랩 등록 성공"),
            @ApiResponse(responseCode = "409", description = "이미 스크랩된 상품")
    })
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> addScrap(
            @Parameter(name = "auctionId", description = "스크랩할 경매 상품의 ID", required = true)
            @PathVariable Long auctionId
    ) {
        boolean addedScrap = scrapService.addScrap(auctionId);

        if (addedScrap) {
            return ResponseEntity.ok(BaseResponse.success("스크랩 등록 완료", null));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.error("이미 스크랩된 상품입니다.", null));
        }
    }

    @Operation(
            summary = "스크랩 취소",
            description = "스크랩된 경매 상품을 취소합니다. 스크랩되지 않은 상품일 경우 409(CONFLICT)를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스크랩 취소 성공"),
            @ApiResponse(responseCode = "409", description = "스크랩되지 않은 상품입니다.")
    })
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> removeScrap(
            @Parameter(name = "auctionId", description = "스크랩 취소할 경매 상품의 ID", required = true)
            @PathVariable Long auctionId
    ) {
        boolean removedScrap = scrapService.removeScrap(auctionId);

        if (removedScrap) {
            return ResponseEntity.ok(BaseResponse.success("스크랩 취소 완료", null));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(BaseResponse.error("스크랩된 상태가 아닙니다.", null));
        }
    }
}
