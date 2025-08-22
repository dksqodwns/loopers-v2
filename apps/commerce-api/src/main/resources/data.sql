-- 사용자 데이터 (login_id -> loginId 컬럼명 수정)
INSERT INTO members (id, loginId, email, username, birth_date, gender, created_at, updated_at)
VALUES (1, 'testuser', 'test@loopers.com', '테스트유저', '2000-01-01', 'MALE', NOW(), NOW());

-- 브랜드 데이터
INSERT INTO brands (id, name, created_at, updated_at)
VALUES (1, '나이키', NOW(), NOW()),
       (2, '아디다스', NOW(), NOW());

-- 상품 데이터 (brand_id -> ref_brand_id 컬럼명 수정)
INSERT INTO products (id, ref_brand_id, name, price, like_count, created_at, updated_at)
VALUES (1, 1, '나이키 에어포스 1', 139000, 0, NOW(), NOW()),
       (2, 1, '나이키 덩크 로우', 129000, 0, NOW(), NOW()),
       (3, 2, '아디다스 삼바', 139000, 0, NOW(), NOW());

-- 상품 재고 데이터 (누락된 부분 추가)
INSERT INTO product_stock (id, product_id, stock, created_at, updated_at)
VALUES (1, 1, 100, NOW(), NOW()),
       (2, 2, 100, NOW(), NOW()),
       (3, 3, 100, NOW(), NOW());