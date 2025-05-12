USE palgoosam;

-- 회원
INSERT INTO user (name, email, profile_image)
VALUES ('김철수', 'chulsoo@example.com', 'https://img.example.com/profiles/1.jpg'),
       ('이영희', 'younghee@example.com', 'https://img.example.com/profiles/2.jpg'),
       ('박민수', 'minsoo@example.com', 'https://img.example.com/profiles/3.jpg');

-- OAuth 토큰
INSERT INTO user_oauth_token (id, refresh_token)
VALUES (1, 'refresh_token_1'),
       (2, 'refresh_token_2'),
       (3, 'refresh_token_3');

-- 카테고리 (재귀)
INSERT INTO category (id, parent_id, name)
VALUES (1, NULL, '전자기기'),
       (2, 1, '모바일/태블릿'),
       (3, 2, '스마트폰'),
       (4, NULL, '가구/인테리어'),
       (5, 4, '사무용가구'),
       (6, NULL, '패션/의류'),
       (7, 6, '여성의류'),
       (8, 7, '아우터');

-- 경매
INSERT INTO auction (seller_id, category_id, title, description, base_price, item_condition, start_time, end_time)
VALUES (1, 3, '삼성 갤럭시 S22 미개봉', '미개봉 갤럭시 S22입니다.', 850000, 'brand_new', '2025-05-01 09:00:00', '2025-05-01 18:00:00'),
       (2, 5, '화이트 책상 팝니다', '사용감 거의 없는 화이트 책상입니다.', 50000, 'gently_used', '2025-05-01 10:00:00',
        '2025-05-01 17:30:00'),
       (3, 8, '겨울용 롱코트', '두 번 착용한 코트입니다.', 60000, 'like_new', '2025-05-01 11:00:00', '2025-05-01 16:00:00');

-- 경매 이미지
INSERT INTO auction_image (auction_id, url, original_name, store_name, image_seq)
VALUES (1, 'https://img.example.com/auctions/1_1.jpg', 'galaxy.jpg', '1_1.jpg', 0),
       (2, 'https://img.example.com/auctions/2_1.jpg', 'desk.jpg', '2_1.jpg', 0),
       (3, 'https://img.example.com/auctions/3_1.jpg', 'coat.jpg', '3_1.jpg', 0);

-- 입찰
INSERT INTO bid (bidder_id, auction_id, price, created_at, cancelled_at, is_winning, is_deleted)
VALUES (2, 1, 860000, '2025-05-01 10:00:00', NULL, FALSE, FALSE),
       (3, 1, 870000, '2025-05-01 11:00:00', NULL, TRUE, FALSE),
       (1, 2, 51000, '2025-05-01 12:00:00', NULL, TRUE, FALSE);

-- 결제
INSERT INTO payment (buyer_id, seller_id, auction_id, recipient_name, phone_number, address_line1, address_line2,
                     zip_code, final_price, order_number, payment_key)
VALUES (3, 1, 1, '박민수', '010-1111-2222', '서울시 중구 세종대로 123', NULL, '04520', 870000, 'ORD123456', 'PK123456'),
       (1, 2, 2, '김철수', '010-2222-3333', '서울시 강남구 테헤란로 456', '101호', '06120', 51000, 'ORD123457', 'PK123457');

-- 배송 주소
INSERT INTO delivery_address (user_id, name, phone_number, address_line1, address_line2, zip_code, is_default)
VALUES (1, '김철수', '010-2222-3333', '서울시 강남구 테헤란로 456', '101호', '06120', TRUE),
       (3, '박민수', '010-1111-2222', '서울시 중구 세종대로 123', NULL, '04520', TRUE);

-- 알림 타입
INSERT INTO notification_type (type)
VALUES ('경매 시작'),
       ('경매 종료'),
       ('경매 상품 낙찰');

-- 알림 이력
INSERT INTO notification_history (user_id, notification_type_id, auction_id, is_read, is_deleted)
VALUES (1, 1, 1, FALSE, FALSE),
       (2, 2, 1, TRUE, FALSE),
       (3, 3, 2, TRUE, FALSE);

-- 알림 토큰
INSERT INTO user_fcm_token (user_id, token)
VALUES (1, 'token_user1'),
       (2, 'token_user2'),
       (3, 'token_user3');

-- 스크랩
INSERT INTO scrap (user_id, auction_id)
VALUES (2, 1),
       (1, 3),
       (3, 2);

-- 검색 기록
INSERT INTO search_history (user_id, keyword)
VALUES (1, '갤럭시 미개봉'),
       (2, '책상 중고'),
       (3, '롱코트 겨울');