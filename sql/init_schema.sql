-- 데이터베이스를 완전히 삭제하고 새로 생성하려면 아래 주석을 해제하여 실행하세요.
-- 기존 데이터가 모두 삭제되므로 주의하세요.
# DROP DATABASE palgoosam;
# CREATE DATABASE palgoosam DEFAULT CHARACTER SET UTF8;

USE palgoosam;

-- 회원 테이블
CREATE TABLE user
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(50)  NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    profile_image VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 구글 OAuth Token 테이블
CREATE TABLE user_oauth_token
(
    id            BIGINT       NOT NULL,
    refresh_token VARCHAR(255) NOT NULL,
    FOREIGN KEY (id) REFERENCES user (id)
);

-- 카테고리 테이블
CREATE TABLE category
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT DEFAULT NULL,
    name      VARCHAR(20) NOT NULL,
    FOREIGN KEY (parent_id) REFERENCES category (id)
);

-- 경매 상품 정보 테이블
CREATE TABLE auction
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    seller_id      BIGINT       NOT NULL,
    category_id    BIGINT       NOT NULL,
    title          VARCHAR(100) NOT NULL,
    description    TEXT         NOT NULL,
    base_price     INT          NOT NULL,
    item_condition VARCHAR(20)  NOT NULL COMMENT 'brand_new, like_new, gently_used, heavily_used, damaged',
    start_time     TIMESTAMP    NOT NULL,
    end_time       TIMESTAMP    NOT NULL,
    status         VARCHAR(20) DEFAULT 'pending' COMMENT 'pending, active, completed, cancelled',
    created_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES user (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

-- 경매 이미지 테이블
CREATE TABLE auction_image
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    auction_id    BIGINT       NOT NULL,
    url           VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    store_name    VARCHAR(255) NOT NULL,
    image_seq     INT          NOT NULL,
    is_main       BOOLEAN   DEFAULT FALSE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (auction_id) REFERENCES auction (id)
);

-- 입찰 테이블
CREATE TABLE bid
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    bidder_id    BIGINT NOT NULL,
    auction_id   BIGINT NOT NULL,
    price        INT    NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    cancelled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_winning   BOOLEAN   DEFAULT FALSE,
    is_deleted   BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (bidder_id) REFERENCES user (id),
    FOREIGN KEY (auction_id) REFERENCES auction (id)
);

-- 주문 테이블
CREATE TABLE payment
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    buyer_id       BIGINT       NOT NULL,
    seller_id      BIGINT       NOT NULL,
    auction_id     BIGINT       NOT NULL,
    recipient_name VARCHAR(50)  NOT NULL,
    phone_number   VARCHAR(20)  NOT NULL,
    address_line1  VARCHAR(255) NOT NULL,
    address_line2  VARCHAR(255),
    zip_code       VARCHAR(20)  NOT NULL,
    final_price    INT          NOT NULL,
    approved_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    order_number   VARCHAR(50)  NOT NULL,
    payment_key    VARCHAR(255) NOT NULL,
    FOREIGN KEY (buyer_id) REFERENCES user (id),
    FOREIGN KEY (seller_id) REFERENCES user (id),
    FOREIGN KEY (auction_id) REFERENCES auction (id)
);

-- 배송 주소 테이블
CREATE TABLE delivery_address
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    name          VARCHAR(20)  NOT NULL,
    phone_number  VARCHAR(20)  NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    zip_code      VARCHAR(20)  NOT NULL,
    is_default    BOOLEAN   DEFAULT FALSE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

-- 알림 타입 테이블
CREATE TABLE notification_type
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) NOT NULL
);

-- 알림 이력 테이블
CREATE TABLE notification_history
(
    id                   BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id              BIGINT NOT NULL,
    notification_type_id BIGINT NOT NULL,
    auction_id           BIGINT NOT NULL,
    is_read              BOOLEAN   DEFAULT FALSE,
    is_deleted           BOOLEAN   DEFAULT FALSE,
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (notification_type_id) REFERENCES notification_type (id),
    FOREIGN KEY (auction_id) REFERENCES auction (id)
);

-- 알림 토큰 테이블
CREATE TABLE user_notification_token
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(500) NOT NULL,
    is_active  BOOLEAN   DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMP,
    is_expired BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

-- 찜(스크랩) 테이블
CREATE TABLE scrap
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    auction_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (auction_id) REFERENCES auction (id)
);

-- 검색 기록 테이블
CREATE TABLE search_history
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    description VARCHAR(100),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted  BOOLEAN   DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES user (id)
);

ALTER TABLE auction ADD FULLTEXT(title, description) with parser ngram;

show databases;