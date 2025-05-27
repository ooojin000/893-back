package com.samyookgoo.palgoosam.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // 인증/권한
    UNAUTHORIZED_USER(401, "A_001", "클라이언트 인증 부재, 로그인 해주세요."),
    FORBIDDEN_BID_CANCEL(403, "A_002", "본인의 입찰만 취소할 수 있습니다."),

    // 유저/카테고리/경매/입찰 조회 실패
    USER_NOT_FOUND(404, "U_001", "유저를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(404, "C_001", "카테고리가 존재하지 않습니다."),
    AUCTION_NOT_FOUND(404, "AUC_001", "경매 상품이 존재하지 않습니다."),
    BID_NOT_FOUND(404, "BID_001", "해당 입찰 내역이 존재하지 않습니다."),
    ADDRESS_NOT_FOUND(404, "ADR_001", "변경할 주소를 찾을 수 없습니다."),
    DELETE_ADDRESS_NOT_FOUND(404, "ADR_002", "삭제할 주소를 찾을 수 없습니다."),

    // 경매 등록/수정 검증
    AUCTION_UPDATE_LOCKED(400, "AUC_002", "경매 시작 10분 전부터는 수정이 불가능합니다."),
    AUCTION_TIME_REQUIRED(400, "AUC_003", "경매 시작 시간 및 경매 소요 시간은 필수입니다."),
    AUCTION_MAIN_IMAGE_REQUIRED(400, "AUC_004", "대표 이미지를 반드시 포함해야 합니다. (imageSeq == 0)"),
    AUCTION_SHOULD_BE_LEAF_CATEGORY(400, "AUC_005", "소분류 카테고리만 선택할 수 있습니다."),
    AUCTION_BASE_PRICE_INVALID(400, "AUC_006", "시작가는 0원 이상이어야 합니다."),
    AUCTION_OPEN_TIME_INVALID(400, "AUC_007", "경매 오픈 시간은 현재 시각 이후부터 24시간 이내여야 합니다."),
    AUCTION_DURATION_INVALID(400, "AUC_008", "경매 소요 시간은 10분 이상, 24시간(1440분) 이내여야 합니다."),

    // 입찰 관련
    BID_TIME_INVALID(400, "BID_002", "현재는 입찰 가능한 시간이 아닙니다."),
    BID_LESS_THAN_BASE(400, "BID_003", "시작가보다 높은 금액을 입력해야 합니다."),
    BID_NOT_HIGHEST(400, "BID_004", "현재 최고가보다 높은 금액을 입력해야 합니다."),
    BID_ALREADY_CANCELED(400, "BID_005", "이미 취소된 입찰입니다."),
    BID_CANCEL_EXPIRED(400, "BID_006", "입찰 후 1분 이내에만 취소할 수 있습니다."),
    BID_CANCEL_NOT_TOP(400, "BID_007", "최고 입찰가가 아니면 취소할 수 없습니다."),

    // FCM
    FCM_TOKEN_MISSING(404, "FCM_001", "토큰이 없습니다."),
    FCM_TOKEN_INVALID(400, "FCM_002", "유효하지 않은 토큰입니다."),

    // 결제
    TOSS_PAYMENT_ERROR(500, "PAY_001", "Toss 결제 오류가 발생했습니다."),

    INTERNAL_SERVER_ERROR(500, "SYS_001", "서버가 오류가 발생했습니다."),
    METHOD_NOT_ALLOWED(405, "SYS_002", "API는 열려있으나 메소드는 사용 불가합니다."),
    INVALID_INPUT_VALUE(400, "SYS_003", "적절하지 않은 요청 값입니다."),
    INVALID_TYPE_VALUE(400, "SYS_004", "요청 값의 타입이 잘못되었습니다."),
    ENTITY_NOT_FOUND(400, "SYS_005", "지정한 Entity를 찾을 수 없습니다."),
    AUTH_ERROR(400, "AU_001", "인증 관련 오류가 발생했습니다.");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
